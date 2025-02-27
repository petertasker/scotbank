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
    public List<Transaction> fetchData() throws IOException, XMLStreamException {
        List<Transaction> allTransactions = new ArrayList<>();
        int page = 0;
        boolean hasMorePages = true;

        try {
            HttpResponse<String> response = Unirest.get("https://api.asep-strath.co.uk/api/transactions").asString();

            if (!response.isSuccess()) {
                throw new XMLStreamException("Failed to fetch transactions: " + response.getStatus());
            }

            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.registerModule(new JodaModule());

            while (hasMorePages) {
                response = unirestWrapper.get("https://api.asep-strath.co.uk/api/transactions?page=" + page);

                if (!response.isSuccess()) {
                    hasMorePages = false;
                } else {
                    XmlParser pageResult = xmlMapper.readValue(response.getBody(), XmlParser.class);
                    List<Transaction> pageTransactions = pageResult.getTransactions();

                    if (pageTransactions == null || pageTransactions.isEmpty()) {
                        hasMorePages = false;
                    } else {
                        allTransactions.addAll(pageTransactions);
                        page = pageResult.getPage();
                        pageResult.setPage(++page);
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
