package uk.co.asepstrath.bank.services.reward;

import org.slf4j.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.*;

public class RewardFetchService {
    private final DataSource datasource;
    private final Logger logger;
    private static final String API_URL = "https://api.asep-strath.co.uk/api/rewards";

    public RewardFetchService(DataSource datasource, Logger logger) {
        this.datasource = datasource;
        this.logger = logger;
    }

    /**
     * Fetches rewards from the API and stores them in the database
     */
    public List<Map<String, Object>> fetchAndStoreRewards() {
        logger.info("Starting to fetch rewards from API...");
        List<Map<String, Object>> rewards = new ArrayList<>();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("Successfully fetched rewards from API.");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.body());

            try (Connection conn = datasource.getConnection()) {
                for (JsonNode node : jsonNode) {
                    Map<String, Object> reward = new HashMap<>();
                    String name = node.get("Name").asText();
                    String description = node.get("Description").asText();
                    double value = node.get("RewardValue").asDouble();
                    double chance = node.get("Chance").asDouble();

                    reward.put("Name", name);
                    reward.put("Description", description);
                    reward.put("RewardValue", value);
                    reward.put("Chance", chance);
                    rewards.add(reward);


                    upsertReward(conn, name, description, value, chance);
                    logger.info("Stored reward in DB: {}", name);
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching rewards from API, loading from database instead.", e);
            return getRewardsFromDatabase();
        }
        logger.info("Successfully fetched and stored {} rewards.", rewards.size());
        return rewards;
    }

    /**
     * Retrieves all rewards from the database
     */
    public List<Map<String, Object>> getRewardsFromDatabase() {
        List<Map<String, Object>> rewards = new ArrayList<>();
        try (Connection conn = datasource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Rewards");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> reward = new HashMap<>();
                reward.put("Name", rs.getString("Name"));
                reward.put("Description", rs.getString("Description"));
                reward.put("RewardValue", rs.getDouble("Reward_value"));
                reward.put("Chance", rs.getDouble("Chance"));
                rewards.add(reward);
            }

            logger.info("Fetched {} rewards from the database.", rewards.size());

        } catch (SQLException e) {
            logger.error("Error fetching rewards from database", e);
        }
        return rewards;
    }

    /**
     * Inserts or updates a reward in the database (Upsert Logic)
     */
    private void upsertReward(Connection conn, String name, String description, double value, double chance) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO Rewards (Name, Description, RewardValue, Chance) " +
                        "VALUES (?, ?, ?, ?) ON CONFLICT(Name) DO UPDATE SET " +
                        "Description = excluded.Description, RewardValue = excluded.RewardValue, Chance = excluded.Chance")) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setDouble(3, value);
            stmt.setDouble(4, chance);
            stmt.executeUpdate();

            logger.info("Inserted/Updated reward: Name={}, Description={}, Value=£{}, Chance={}%",
                    name, description, value, chance);
        }
    }
}
