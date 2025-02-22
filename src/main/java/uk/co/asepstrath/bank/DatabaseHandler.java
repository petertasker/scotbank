package uk.co.asepstrath.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;

public class DatabaseHandler {

    private static final String SQL_INSERT_ACCOUNT =
            "INSERT INTO Accounts (AccountID, Balance, Name, RoundUpEnabled) VALUES (?, ?, ?, ?)";

    private static final String SQL_INSERT_BUSINESS =
            "INSERT INTO Businesses (BusinessID, BusinessName, Category, Sanctioned ) VALUES (?, ?, ?, ?)";

    private static final String SQL_INSERT_TRANSACTION =
            "INSERT INTO Transactions (Timestamp, Amount, SenderID, TransactionID, ReceiverID, TransactionType)\n" +
                                                         "VALUES (?, ?, ?, ?, ?, ?)";

    private final Logger log;


    public DatabaseHandler() {
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
            log.info("Inserted Transaction: ID: {}, from {}, to {}, amount £{}", transaction.getId(), transaction.getFrom(), transaction.getTo(), transaction.getAmount());

            UpdateAccountBalance(connection, transaction);
        }
        catch (SQLException e) {
            log.info("Transaction Declined: {}", e.getMessage());
        }
    }

    void UpdateAccountBalance(Connection connection, Transaction transaction) throws SQLException {
        Account senderAccount = FetchAccount(connection, transaction.getFrom());
        if (senderAccount == null) {
            throw new SQLException("Account not found" + transaction.getFrom());
        }
        try {
            senderAccount.withdraw(transaction.getAmount());
            UpdateAccountDB(connection, senderAccount);
        }catch (ArithmeticException e) {
            log.info("{}", e.getMessage());
        }
    }

    private Account FetchAccount(Connection connection, String accountID) throws SQLException {
        String SQL_FETCH_ACCOUNT = "SELECT * FROM Accounts WHERE AccountID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_FETCH_ACCOUNT)) {
            preparedStatement.setString(1, accountID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String AccountID = resultSet.getString("AccountID");
                BigDecimal Balance = resultSet.getBigDecimal("Balance");
                String Name = resultSet.getString("Name");
                boolean RoundUpEnabled = resultSet.getBoolean("RoundUpEnabled");
                return new Account(AccountID,Name,Balance,RoundUpEnabled);
            }
        }
        return null; // if account was not found
    }

    private void UpdateAccountDB(Connection connection, Account account) throws SQLException {
        String UPDATE_ACCCOUNT_SQL = "UPDATE Accounts SET Balance = ? WHERE AccountID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ACCCOUNT_SQL)) {
            preparedStatement.setBigDecimal(1, account.getBalance());
            preparedStatement.setString(2, account.getAccountID());
            preparedStatement.executeUpdate();
            log.info("Updated Account : {}, New Balance: £{}", account.getAccountID(), account.getBalance());
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
            log.info("Inserted Account: {}, Balance: £{}", account.getAccountID(), account.getBalance());
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
}
