package uk.co.asepstrath.bank.services.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jooby.StatusCode;
import io.jooby.exception.StatusCodeException;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.UnirestException;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Reward;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fetches Rewards data from an external API or database.
 */
public class RewardDataService extends DataService implements DataServiceFetcher<Reward> {
    private Connection connection; // Database connection

    // Constructor for API fetching (default)
    public RewardDataService(Logger logger, UnirestWrapper unirestWrapper, ObjectMapper objectMapper,
                             DataSource dataSource) {
        super(logger, unirestWrapper, objectMapper, dataSource);
    }

    /**
     * Fetches a list of rewards from the API.
     *
     * @return A list of Rewards objects
     * @throws IOException if the API request fails
     */
    @Override
    public List<Reward> fetchData() throws IOException {
        try {
            HttpResponse<String> response = unirestWrapper.get("https://api.asep-strath.co.uk/api/rewards");
            if (response.isSuccess()) {
                logger.info("Successfully retrieved rewards data");
                return objectMapper.readValue(response.getBody(), new TypeReference<>() {
                });
            }
            else {
                throw new IOException("Failed to fetch rewards: " + response.getStatus());
            }
        }
        catch (IOException e) {
            logger.error("Error fetching rewards data from API", e);
            throw e;
        }
    }

    public String postReward(Reward reward, String accountId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        try {
            HttpResponse<String> response = unirestWrapper.post(
                    "https://api.asep-strath.co.uk/api/rewards",
                    reward.toJson(accountId),
                    headers
            );
            return response.getBody();
        }
        catch (UnirestException e) {
            throw new StatusCodeException(StatusCode.BAD_REQUEST);
        }
    }
}
