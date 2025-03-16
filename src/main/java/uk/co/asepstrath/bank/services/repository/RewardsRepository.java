package uk.co.asepstrath.bank.services.repository;

import org.slf4j.Logger;
import uk.co.asepstrath.bank.Rewards;

import javax.sql.DataSource;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The Rewards repository service
 */
public class RewardsRepository extends BaseRepository {

    private static final String SQL_CREATE_TABLE_REWARDS = """
                CREATE TABLE Rewards (
                Name TEXT PRIMARY KEY NOT NULL,
                Description TEXT,
                RewardValue DECIMAL(5,2) NOT NULL,
                Chance DECIMAL(5,2) NOT NULL)
                """;

    private static final String SQL_CREATE_TABLE_USER_REWARDS = """
                CREATE TABLE IF NOT EXISTS UserRewards (
                AccountID TEXT NOT NULL,
                RewardName TEXT NOT NULL,
                WonAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (AccountID) REFERENCES Accounts(AccountID),
                FOREIGN KEY (RewardName) REFERENCES Rewards(Name)
            )
            """;

    private static final String SQL_GET_ALL_REWARDS =
            "SELECT * FROM Rewards";

    private static final String SQL_INSERT_USER_REWARD =
            "INSERT INTO UserRewards (AccountID, RewardName) VALUES (?, ?)";

    public RewardsRepository(Logger logger) {
        super(logger);
    }

    /**
     * Creates the rewards tables
     *
     * @param connection Database connection
     * @throws SQLException Database connection failure
     */
    public void createTables(Connection connection) throws SQLException {
        executeUpdate(connection, SQL_CREATE_TABLE_REWARDS);
        executeUpdate(connection, SQL_CREATE_TABLE_USER_REWARDS);
    }
    /**
     * Inserts a reward into the database
     *
     * @param connection Database connection
     * @param reward     Reward object to insert
     * @throws SQLException Database failure
     */
    public void insert(Connection connection, Rewards reward) throws SQLException {
        String sql = "INSERT INTO Rewards (Name, Description, RewardValue, Chance) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT(Name) DO UPDATE SET Description = EXCLUDED.Description, " +
                "RewardValue = EXCLUDED.RewardValue, Chance = EXCLUDED.Chance";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, reward.getRewardsName());
            stmt.setString(2, reward.getRewardsDescription());
            stmt.setBigDecimal(3, reward.getRewardsValue());
            stmt.setDouble(4, reward.getRewardsChance());
            stmt.executeUpdate();
            logger.info("Inserted reward: {}", reward.getRewardsName());
        }
    }

    /**
     * Retrieves all available rewards from the database
     *
     * @param connection Database connection
     * @return List of rewards
     * @throws SQLException Database failure
     */
    public List<Rewards> getAllRewards(Connection connection) throws SQLException {
        List<Rewards> rewards = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_GET_ALL_REWARDS);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                rewards.add(new Rewards(
                        resultSet.getString("Name"),
                        resultSet.getString("Description"),
                        resultSet.getBigDecimal("RewardValue"),
                        resultSet.getDouble("Chance")
                ));
            }
        }
        return rewards;
    }

    /**
     * Inserts a record of a user winning a reward
     *
     * @param connection Database connection
     * @param userId     User identifier
     * @param rewardName Reward name
     * @throws SQLException Database failure
     */
    public void insertUserReward(Connection connection, String userId, String rewardName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT_USER_REWARD)) {
            statement.setString(1, userId);
            statement.setString(2, rewardName);
            statement.executeUpdate();
            logger.info("User {} won reward: {}", userId, rewardName);
        }
    }
}
