package uk.co.asepstrath.bank.services.repository;

import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.DataAccessException;
import uk.co.asepstrath.bank.Manager;
import uk.co.asepstrath.bank.services.login.HashingPasswordService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The Manager repository service
 */
public class ManagerRepository extends BaseRepository {

    public ManagerRepository(Logger logger) {
        super(logger);
    }

    private static final String SQL_CREATE_TABLE = """
        CREATE TABLE Managers (
            ManagerID VARCHAR(255) NOT NULL,
            Name VARCHAR(255) NOT NULL,
            Password VARCHAR(512) NOT NULL,
            PRIMARY KEY (ManagerID)
        )
        """;


    private static final String SQL_INSERT_MANAGER =
            "INSERT INTO Managers (ManagerID, Name, Password) VALUES (?, ?, ?)";

    private static final String SQL_SELECT_ALL_ACCOUNTS =
            "SELECT AccountID, Name, Balance, RoundUpEnabled FROM Accounts";


    /**
     * Creates the Manager table
     * @param connection Database connection
     * @throws SQLException Database connection failure
     */
    public void createTable(Connection connection) throws SQLException {
        executeUpdate(connection, SQL_CREATE_TABLE);
    }

    /**
     * Inserts a Manager into the Manager table
     * @param connection Database Connection
     * @param manager manager object
     * @throws SQLException Database connection failure
     */
    public void insert(Connection connection, Manager manager, String password) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        String hashedPassword = HashingPasswordService.hashPassword(password);
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT_MANAGER)) {
            statement.setString(1, manager.getManagerID());
            statement.setString(2, manager.getName());
            statement.setString(3, hashedPassword);
            statement.executeUpdate();
            logger.info("Manager: {}, Name: {}, Password: {}", manager.getManagerID(), manager.getName(), password);
        }
    }

    /**
     * Selects all accounts from the database
     * @param connection Database connection
     * @return List of Account objects
     * @throws SQLException Database connection failure
     */
    public List<Account> getAllAccounts(Connection connection) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        try (ResultSet resultSet = connection.createStatement().executeQuery(SQL_SELECT_ALL_ACCOUNTS)) {
            while (resultSet.next()) {
                accounts.add(
                        new Account(
                            resultSet.getString("AccountID"),
                            resultSet.getString("Name"),
                            resultSet.getBigDecimal("Balance"),
                            resultSet.getBoolean("RoundUpEnabled")
                    )
                );
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DataAccessException("Failed to retrieve accounts from the database: ",e);
        }
        logger.info("Returning accounts from the database");
        return accounts;
    }
}
