package uk.co.asepstrath.bank.services.rewards;
import uk.co.asepstrath.bank.services.repository.RewardsRepository;
import org.slf4j.Logger;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.sql.SQLException;

public class RewardSpinService {
    private final DataSource datasource;
    private final Logger logger;
    private final RewardsRepository rewardsRepository;
    public RewardSpinService(DataSource datasource, Logger logger) {
        this.datasource = datasource;
        this.logger = logger;
        this.rewardsRepository = new RewardsRepository(logger);
    }

    /**
     * Process the user spinning the wheel (user MUST win a reward)
     */
    public String processSpin(String userId) throws SQLException {
        if (hasUserSpunToday(userId)) {
            return "You have already spun the wheel today. Try again tomorrow!";
        }

        List<Map<String, Object>> rewards = getAvailableRewards();
        Map<String, Object> selectedReward = selectWeightedRandomReward(rewards);

        assignRewardToUser(userId, (String) selectedReward.get("Name"));
        return "Congratulations! You won: " + selectedReward.get("Name");
    }

    /**
     * Check if the user has already spun the wheel today
     */
    private boolean hasUserSpunToday(String userId) throws SQLException {
        try (Connection conn = datasource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT 1 FROM UserRewards WHERE AccountID = ? AND DATE(WonAt) = CURRENT_DATE")) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     *  Get available rewards from the database
     */
    private List<Map<String, Object>> getAvailableRewards() throws SQLException {
        List<Map<String, Object>> rewards = new ArrayList<>();

        try (Connection conn = datasource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Rewards");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> reward = new HashMap<>();
                reward.put("Name", rs.getString("Name"));
                reward.put("Description", rs.getString("Description"));
                reward.put("RewardValue", rs.getBigDecimal("RewardValue"));
                reward.put("Chance", rs.getDouble("Chance"));
                rewards.add(reward);
            }
        }
        return rewards;
    }

    /**
     * Selects a reward based on weighted probability
     */
    private Map<String, Object> selectWeightedRandomReward(List<Map<String, Object>> rewards) {
        double totalWeight = 0.0;
        for (Map<String, Object> reward : rewards) {
            totalWeight += (double) reward.get("Chance");
        }

        double random = new Random().nextDouble() * totalWeight;
        double cumulativeWeight = 0.0;

        for (Map<String, Object> reward : rewards) {
            cumulativeWeight += (double) reward.get("Chance");
            if (random <= cumulativeWeight) {
                return reward; // User wins this reward
            }
        }

        // Fallback in case of calculation issues (should not happen)
        return rewards.get(rewards.size() - 1);
    }
    public String getUserRewardHistory(String userId) throws SQLException {
        StringBuilder history = new StringBuilder("Reward History:\n");

        try (Connection conn = datasource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT RewardName, won_at FROM UserRewards WHERE AccountID = ? ORDER BY WonAt DESC")) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    history.append(" - ").append(rs.getString("RewardName"))
                            .append(" (").append(rs.getString("WonAt")).append(")\n");
                }
            }
        }
        return history.toString();
    }

    /**
     * Assigns a reward to the user
     */
    private void assignRewardToUser(String userId, String rewardName) throws SQLException {
        try (Connection conn = datasource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO UserRewards (AccountID, RewardName, WonAt) VALUES (?, ?, CURRENT_TIMESTAMP)")) {
            stmt.setString(1, userId);
            stmt.setString(2, rewardName);
            stmt.executeUpdate();
            logger.info("User {} won reward: {}", userId, rewardName);
        }
    }
}
