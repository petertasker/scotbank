package uk.co.asepstrath.bank.services.data;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.core.HttpResponse;
import uk.co.asepstrath.bank.Account;

import java.io.IOException;
import java.util.List;

/**
 * Fetches Account data from external API
 */
public class AccountDataService implements DataService<Account> {
    private final ObjectMapper mapper;
    private UnirestWrapper unirestWrapper;

    public AccountDataService() {
        this.mapper = new ObjectMapper();
        this.unirestWrapper = new UnirestWrapper();
    }

    public void setUnirestWrapper(UnirestWrapper wrapper) {
        this.unirestWrapper = wrapper;
    }

    /**
     * Gets account data from the API
     * @return List of Account objects
     * @throws IOException Failed to fetch data from API
     */
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