package uk.co.asepstrath.bank.services.repository;

import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The Account repository service
 */
public class AccountRepository extends BaseRepository {

    private static final String SQL_CREATE_TABLE = """
            CREATE TABLE Accounts (
            AccountID VARCHAR(255) NOT NULL,
            Balance DECIMAL(12,2) NOT NULL,
            Name VARCHAR(255) NOT NULL,
            RoundUpEnabled BIT NOT NULL,
            PRIMARY KEY (AccountID)
        )
        """;

    private static final String SQL_INSERT_ACCOUNT =
            "INSERT INTO Accounts (AccountID, Balance, Name, RoundUpEnabled) VALUES (?, ?, ?, ?)";

    private static final String SQL_UPDATE_BALANCE =
            "UPDATE Accounts SET Balance = ? WHERE AccountID = ?";

    private static final String SQL_GET_ACCOUNT =
            "SELECT Balance, Name, RoundUpEnabled FROM Accounts WHERE AccountID = ?";

    public AccountRepository(Logger logger) {
        super(logger);
    }

    /**
     * Creates the Account table
     * @param connection Database connection
     * @throws SQLException Database connection failure
     */
    public void createTable(Connection connection) throws SQLException {
        executeUpdate(connection, SQL_CREATE_TABLE);
    }

    /**
     * Inserts an Account into the Account table
     * @param connection Database Connection
     * @param account An Account object
     * @throws SQLException Database connection failure
     */
    public void insert(Connection connection, Account account) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT_ACCOUNT)) {
            statement.setString(1, account.getAccountID());
            statement.setBigDecimal(2, account.getBalance());
            statement.setString(3, account.getName());
            statement.setBoolean(4, account.isRoundUpEnabled());
            statement.executeUpdate();
        }
    }

    /**
     * Updates the balance of an Account
     * @param connection Database connection
     * @param account An Account object
     * @throws SQLException Database connection failure
     */
    public void updateBalance(Connection connection, Account account) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_BALANCE)) {
            statement.setBigDecimal(1, account.getBalance());
            statement.setString(2, account.getAccountID());
            statement.executeUpdate();
        }
    }

    /**
     * Gets an Account object from the database
     * @param connection Database connection
     * @param accountID Unique identifier of an account
     * @return An Account object parsed from the database
     * @throws SQLException Database connection failure
     */
    public Account getAccount(Connection connection, String accountID) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_GET_ACCOUNT)) {
            statement.setString(1, accountID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    BigDecimal balance = resultSet.getBigDecimal("Balance");
                    String name = resultSet.getString("Name");
                    boolean roundUpEnabled = resultSet.getBoolean("RoundUpEnabled");
                    return new Account(accountID, name, balance, roundUpEnabled);
                }
            }
        }
        return null;
    }
}
