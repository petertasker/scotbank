package uk.co.asepstrath.bank.services.data;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import kong.unirest.core.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.asepstrath.bank.Transaction;
import uk.co.asepstrath.bank.parsers.XmlParser;

import javax.sql.DataSource;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fetches Transaction data from external API
 */
public class TransactionDataService extends DataService implements DataServiceFetcher<Transaction> {
    private final Logger logger;
    private Connection connection;

    public TransactionDataService(UnirestWrapper unirestWrapper) {
        super(unirestWrapper);
        this.logger = LoggerFactory.getLogger(TransactionDataService.class);
    }

    public TransactionDataService(DataSource dataSource) throws SQLException {
        super(new UnirestWrapper());
        this.connection = dataSource.getConnection();
        this.logger = LoggerFactory.getLogger(TransactionDataService.class);
    }

    /**
     * Fetches a List of Transactions from an external API
     *
     * @return A list of transaction objects
     * @throws XMLStreamException failed to parse XML data from API
     */
    @Override
    public List<Transaction> fetchData() throws XMLStreamException {
        List<Transaction> allTransactions = new ArrayList<>();
        XmlMapper xmlMapper = configureXmlMapper();

        int page = 0;
        boolean hasMorePages = true;

        try {
            logger.info("Fetching transactions from database...");
            while (hasMorePages) {

                // Make GET request for each page
                HttpResponse<String> response = fetchPage(page);

                if (!response.isSuccess()) {
                    logger.info("Failed to fetch page {}: {}", page, response.getStatus());
                    break;
                }

                // Map page responses to a List of Transaction Objects
                XmlParser pageResult = parseResponse(xmlMapper, response);
                List<Transaction> pageTransactions = processTransactions(pageResult);

                // Break if for some reason the XML is configured poorly, and the parser is sent to an empty page
                if (pageTransactions.isEmpty()) {
                    logger.info("No more transactions found on page {}", page);
                    break;
                }

                // Append page Transactions to List of all Transactions
                allTransactions.addAll(pageTransactions);
                hasMorePages = determineIfMorePages(pageResult, page);

                // Go to next page
                if (hasMorePages) {
                    page++;
                }
                else {
                    logger.info("Reached last page ({}) of transactions", page);
                }
            }
        }
        catch (IOException e) {
            throw new XMLStreamException("Failed to parse XML", e);
        }
        logger.info("Successfully fetched transactions data");
        return allTransactions;
    }

    /**
     * Configures and returns an XmlMapper with necessary modules
     */
    private XmlMapper configureXmlMapper() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JodaModule());
        return xmlMapper;
    }

    /**
     * Fetches a single page of transaction data
     */
    private HttpResponse<String> fetchPage(int page) {
        return unirestWrapper.get("https://api.asep-strath.co.uk/api/transactions", "page", page);
    }

    /**
     * Parses the API response into an XmlParser object
     */
    private XmlParser parseResponse(XmlMapper xmlMapper, HttpResponse<String> response) throws IOException {
        return xmlMapper.readValue(response.getBody(), XmlParser.class);
    }

    /**
     * Processes transactions from a page result
     */
    private List<Transaction> processTransactions(XmlParser pageResult) {
        return pageResult.getTransactions().stream().map(this::createTransactionSafely).filter(Objects::nonNull)
                .toList();
    }

    /**
     * Safely creates a Transaction object, handling errors
     */
    private Transaction createTransactionSafely(Transaction transaction) {
        try {
            if (transaction.getAmount() == null) {
                logger.warn("Skipping transaction with null amount: {}", transaction.getId());
                return null;
            }
            return new Transaction(connection, transaction.getTimestamp(), transaction.getAmount(),
                    transaction.getFrom(), transaction.getId(), transaction.getTo(), transaction.getType());
        }
        catch (SQLException e) {
            logger.error("SQL error processing transaction {}: {}", transaction.getId(), e.getMessage());
            return null;
        }
        catch (Exception e) {
            logger.error("Unexpected error processing transaction {}: {}", transaction.getId(), e.getMessage());
            return null;
        }
    }

    /**
     * Determines if there are more pages to fetch
     */
    private boolean determineIfMorePages(XmlParser pageResult, int currentPage) {
        return pageResult.isHasNext() && currentPage < pageResult.getTotalPages() - 1;
    }
}
