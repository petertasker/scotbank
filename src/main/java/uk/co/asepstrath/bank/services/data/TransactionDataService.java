package uk.co.asepstrath.bank.services.data;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.asepstrath.bank.Transaction;
import uk.co.asepstrath.bank.parsers.XmlParser;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

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


    @Override
    public List<Transaction> fetchData() throws XMLStreamException {
        List<Transaction> allTransactions = new ArrayList<>();
        int page = 0;
        boolean hasMorePages = true;

        try {
            // Initialize the HTTP response
            HttpResponse<String> response = unirestWrapper.get("https://api.asep-strath.co.uk/api/transactions");
            if (!response.isSuccess()) {
                throw new XMLStreamException("Failed to fetch transactions: " + response.getStatus());
            }

            // Set up the XML mapper
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.registerModule(new JodaModule());

            // Continue fetching pages until there are no more
            while (hasMorePages) {
                // Fetch the next page
                response = unirestWrapper.get("https://api.asep-strath.co.uk/api/transactions?page=" + page);

                if (!response.isSuccess()) {
                    // Exit if the response is not successful
                    hasMorePages = false;
                    logger.warn("Failed to fetch page {}, exiting loop", page);
                } else {
                    // Parse the response body into XmlParser
                    XmlParser pageResult = xmlMapper.readValue(response.getBody(), XmlParser.class);
                    List<Transaction> pageTransactions = pageResult.getTransactions();

                    // Check if no transactions are found or the list is empty
                    if (pageTransactions == null || pageTransactions.isEmpty()) {
                        hasMorePages = false;  // Exit the loop if no transactions are found
                        logger.info("No more transactions found, exiting loop.");
                    } else {
                        // Add the current page's transactions to the list
                        allTransactions.addAll(pageTransactions);

                        // Get the next page number and increment
                        page = pageResult.getPage() + 1;
                        logger.info("Fetched page {} of {}", pageResult.getPage(), pageResult.getTotalPages());
                    }
                }
            }
        } catch (IOException e) {
            throw new XMLStreamException("Failed to parse XML", e);
        }

        return allTransactions;
    }

}
