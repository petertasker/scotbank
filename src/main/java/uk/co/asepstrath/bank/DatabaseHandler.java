package uk.co.asepstrath.bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {


    private static final String SQL_INSERT_ACCOUNT = """
            INSERT INTO Accounts (AccountID, Balance, Name, RoundUpEnabled) VALUES (?, ?, ?, ?)""";

    private static final String SQL_INSERT_BUSINESS = """
            INSERT INTO Businesses (BusinessID, BusinessName, Category, Sanctioned ) VALUES (?, ?, ?, ?)""";

    private static final String SQL_INSERT_TRANSACTION = """
        INSERT INTO Transactions (Timestamp, Amount, SenderID, TransactionID, ReceiverID, TransactionType)
        VALUES (?, ?, ?, ?, ?, ?)""";

    private final DataSource dataSource;
    private final ObjectMapper mapper;
    private final Logger log;


    public DatabaseHandler(DataSource dataSource) {
        this.dataSource = dataSource;
        this.mapper = new ObjectMapper();
        this.log = LoggerFactory.getLogger(DatabaseHandler.class);
    }


    void insertTransaction(Connection connection, Transaction transaction) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_TRANSACTION)) {
            preparedStatement.setTimestamp(1, new Timestamp(transaction.getTimestamp().getMillis()));
            preparedStatement.setString(2, transaction.getAmount().toString());
            preparedStatement.setString(3, transaction.getFrom());
            preparedStatement.setString(4, transaction.getId());
            preparedStatement.setString(5, transaction.getTo());
            preparedStatement.setString(6, transaction.getType());
            preparedStatement.executeUpdate();
            log.info("Inserted Transaction {}", transaction.getId());
        }
        catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    // Insert account into database
    void insertAccount(Connection connection, Account account) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_ACCOUNT)) {
            preparedStatement.setString(1, account.getAccountID());
            preparedStatement.setBigDecimal(2, account.getBalance());
            preparedStatement.setString(3, account.getName());
            preparedStatement.setBoolean(4, account.isRoundUpEnabled());
            preparedStatement.executeUpdate();
            log.info("Inserted Account: {}", account.getAccountID());
        }
        catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    // Insert business into database
    void insertBusiness(Connection connection, Business business) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_BUSINESS)) {
            preparedStatement.setString(1, business.getID());
            preparedStatement.setString(2, business.getName());
            preparedStatement.setString(3, business.getCategory());
            preparedStatement.setBoolean(4, business.isSanctioned());
            preparedStatement.executeUpdate();
            log.info("Inserted Business: {}", business.getID());
        }
    }


    // Query all accounts
    public List<Account> queryAccounts() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM Accounts";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                accounts.add(new Account(
                        resultSet.getString("AccountID"),
                        resultSet.getString("Name"),
                        resultSet.getBigDecimal("Balance"),
                        resultSet.getBoolean("RoundUpEnabled")
                ));
            }
        }

        return accounts;
    }


    // Query all businesses
    public List<Business> queryBusinesses() throws SQLException {
        List<Business> businesses = new ArrayList<>();

        String query = "SELECT * FROM Business";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                businesses.add(new Business(
                        resultSet.getString("BusinessID"),
                        resultSet.getString("BusinessName"),
                        resultSet.getString("Category"),
                        resultSet.getBoolean("Sanctioned")));
            }
        }

        return businesses;
    }


    // Query all transactions
    public List<Transaction> queryTransactions() throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM Transactions";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                transactions.add(new Transaction(
                        new DateTime(resultSet.getTimestamp("Timestamp").getTime()),
                        resultSet.getInt("Amount"),
                        resultSet.getString("SenderID"),
                        resultSet.getString("TransactionID"),
                        resultSet.getString("ReceiverID"),
                        resultSet.getString("TransactionType")
                ));
            }
        }

        return transactions;
    }


}
