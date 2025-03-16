package uk.co.asepstrath.bank.services.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.core.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.asepstrath.bank.Rewards;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Fetches Rewards data from an external API or database.
 */
public class RewardsDataService extends DataService implements DataServiceFetcher<Rewards> {
    private final Logger logger;
    private final ObjectMapper mapper;
    private Connection connection; // Database connection

    // Constructor for API fetching (default)
    public RewardsDataService() {
        super(new UnirestWrapper());
        this.mapper = new ObjectMapper();
        this.logger = LoggerFactory.getLogger(RewardsDataService.class);
    }

    // Constructor for API fetching (if passing UnirestWrapper)
    public RewardsDataService(UnirestWrapper unirestWrapper) {
        super(unirestWrapper);
        this.mapper = new ObjectMapper();
        this.logger = LoggerFactory.getLogger(RewardsDataService.class);
    }

    // **Second Constructor for Database fetching**
    public RewardsDataService(DataSource dataSource) throws SQLException {
        super(new UnirestWrapper()); // Keep API support
        this.connection = dataSource.getConnection(); // Use database
        this.mapper = new ObjectMapper();
        this.logger = LoggerFactory.getLogger(RewardsDataService.class);
    }

    /**
     * Fetches a list of rewards from the API.
     *
     * @return A list of Rewards objects
     * @throws IOException if the API request fails
     */
    @Override
    public List<Rewards> fetchData() throws IOException {
        UnirestWrapper wrapper = getUnirestWrapper();
        try {
            HttpResponse<String> response = wrapper.get("https://api.asep-strath.co.uk/api/rewards");
            if (response.isSuccess()) {
                logger.info("Successfully retrieved rewards data");
                return mapper.readValue(response.getBody(), new TypeReference<>() {});
            } else {
                throw new IOException("Failed to fetch rewards: " + response.getStatus());
            }
        } catch (IOException e) {
            logger.error("Error fetching rewards data from API", e);
            throw e;
        }
    }

    /**
     * Fetches a list of rewards from the database.
     *
     * @return A list of Rewards objects
     * @throws SQLException if the database query fails
     */
    public List<Rewards> fetchFromDatabase() throws SQLException {
        List<Rewards> rewardsList = new java.util.ArrayList<>();
        String sql = "SELECT name, description, reward_value, chance FROM rewards";

        try (var statement = connection.prepareStatement(sql);
             var resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                rewardsList.add(new Rewards(
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getBigDecimal("reward_value"),
                        resultSet.getDouble("chance")
                ));
            }
            logger.info("Successfully retrieved rewards from the database.");
        } catch (SQLException e) {
            logger.error("Error fetching rewards from database", e);
            throw e;
        }
        return rewardsList;
    }
}
