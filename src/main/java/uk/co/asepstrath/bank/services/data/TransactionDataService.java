package uk.co.asepstrath.bank.services.data;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import kong.unirest.core.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.asepstrath.bank.Transaction;
import uk.co.asepstrath.bank.parsers.XmlParser;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * Fetches Transaction data from external API
 */
public class TransactionDataService implements DataService<Transaction> {

    private final Logger logger;
    private UnirestWrapper unirestWrapper;


    public TransactionDataService() {
        this.logger = LoggerFactory.getLogger(TransactionDataService.class);
        this.unirestWrapper = new UnirestWrapper();
    }

    public void setUnirestWrapper(UnirestWrapper wrapper) {
        this.unirestWrapper = wrapper;
    }


    /**
     * Fetches a List of Transactions from an external API
     * @return A list of transaction objects
     * @throws XMLStreamException failed to parse XML data from API
     */
    @Override
    public List<Transaction> fetchData() throws XMLStreamException {
        List<Transaction> allTransactions = new ArrayList<>();
        int page = 0;
        try {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.registerModule(new JodaModule());

            while (true) {
                HttpResponse<String> response = unirestWrapper.get("https://api.asep-strath.co.uk/api/transactions", "page", page);

                if (!response.isSuccess()) {
                    logger.info("Failed to fetch page {}: {}", page, response.getStatus());
                    break;
                }

                XmlParser pageResult = xmlMapper.readValue(response.getBody(), XmlParser.class);
                List<Transaction> pageTransactions = pageResult.getTransactions();

                if (pageTransactions == null || pageTransactions.isEmpty()) {
                    logger.info("No more transactions found on page {}", page);
                    break;
                }

                allTransactions.addAll(pageTransactions);

                if (!pageResult.isHasNext() || page >= pageResult.getTotalPages() - 1) {
                    logger.info("Reached last page ({})", page);
                    break;
                }

                page++;
                logger.info("Fetched page {} of {}", page, pageResult.getTotalPages());
            }
        } catch (IOException e) {
            throw new XMLStreamException("Failed to parse XML", e);
        }

        return allTransactions;
    }

}
