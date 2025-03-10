package uk.co.asepstrath.bank.services.repository;

import io.jooby.StatusCode;
import io.jooby.exception.StatusCodeException;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.DataAccessException;
import uk.co.asepstrath.bank.Manager;
import uk.co.asepstrath.bank.services.CurrencyFormatter;
import uk.co.asepstrath.bank.services.login.HashingPasswordService;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * The Manager repository service
 */
public class ManagerRepository extends BaseRepository implements CurrencyFormatter {

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
    private static final String SQL_SELECT_TOP_TEN_SPENDERS = """
            SELECT a.Name, a.Postcode, SUM(t.Amount) AS TotalAmount
            FROM Transactions t
            JOIN Accounts a ON t.SenderID = a.AccountID
            WHERE t.TransactionType = 'PAYMENT'
            GROUP BY a.Name, a.Postcode
            ORDER BY TotalAmount DESC
            LIMIT 10;
            """;

    public ManagerRepository(Logger logger) {
        super(logger);
    }

    /**
     * Creates the Manager table
     *
     * @param connection Database connection
     * @throws SQLException Database connection failure
     */
    public void createTable(Connection connection) throws SQLException {
        executeUpdate(connection, SQL_CREATE_TABLE);
    }

    /**
     * Inserts a Manager into the Manager table
     *
     * @param connection Database Connection
     * @param manager    manager object
     * @throws SQLException Database connection failure
     */
    public void insert(Connection connection, Manager manager, String password) throws SQLException,
            NoSuchAlgorithmException, InvalidKeySpecException {
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
     *
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
        }
        catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DataAccessException("Failed to retrieve accounts from the database: ", e);
        }
        logger.info("Returning accounts from the database");
        return accounts;
    }

    public List<Map<String, Object>> getTopTenSpenders(Connection connection) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = SQL_SELECT_TOP_TEN_SPENDERS;

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("Name", rs.getString("Name"));
                row.put("Postcode", rs.getString("Postcode"));
                row.put("TotalAmount", formatCurrency(rs.getBigDecimal("TotalAmount")));
                results.add(row);
            }
        }
        catch (SQLException e) {
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Failed to get top ten spenders");
        }
        return results;
    }

    @Override
    public String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "£0.00"; // Default value if balance is null
        }

        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.UK);
        DecimalFormat decimalFormat = (DecimalFormat) formatter;
        decimalFormat.applyPattern("#,###.00");

        return decimalFormat.format(amount);
    }
}
