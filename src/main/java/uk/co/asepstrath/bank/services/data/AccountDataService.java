package uk.co.asepstrath.bank.services.data;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.asepstrath.bank.Account;

import java.io.IOException;
import java.util.List;

public class AccountDataService implements DataService<Account> {
    private final ObjectMapper mapper;
    private final Logger logger;
    private UnirestWrapper unirestWrapper;

    public AccountDataService() {
        this.mapper = new ObjectMapper();
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.unirestWrapper = new UnirestWrapper();
    }

    public void setUnirestWrapper(UnirestWrapper wrapper) {
        this.unirestWrapper = wrapper;
    }

    @Override
    public List<Account> fetchData() throws IOException {
        try {
            HttpResponse<String> response = unirestWrapper.get("https://api.asep-strath.co.uk/api/accounts");

            if (response.isSuccess()) {
                return mapper.readValue(response.getBody(), new TypeReference<>() {
                });
            } else {
                throw new JsonParseException(null, "Failed to fetch accounts: " + response.getStatus());
            }
        } catch (IOException e) {
            throw new JsonParseException(null, "Failed to parse account data");
        }
    }
}