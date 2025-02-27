package uk.co.asepstrath.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import java.sql.*;
import java.util.Objects;

public class DatabaseHandler {

    private static final String SQL_INSERT_ACCOUNT =
            "INSERT INTO Accounts (AccountID, Balance, Name, RoundUpEnabled) VALUES (?, ?, ?, ?)";

    private static final String SQL_INSERT_BUSINESS =
            "INSERT INTO Businesses (BusinessID, BusinessName, Category, Sanctioned ) VALUES (?, ?, ?, ?)";

    private static final String SQL_INSERT_TRANSACTION =
            "INSERT INTO Transactions (Timestamp, Amount, SenderID, TransactionID, ReceiverAccountID, ReceiverBusinessID, TransactionType, TransactionAccepted)\n" +
                                                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final Logger log;


    public DatabaseHandler() {
        this.log = LoggerFactory.getLogger(DatabaseHandler.class);
    }


    public void insertTransaction(Connection connection, Transaction transaction) throws SQLException {

        // Determine whether transaction succeeds or declines
        Account senderAccount = fetchAccount(connection, transaction.getFrom());
        boolean accepted = true;
        if (senderAccount == null) {
            accepted = false;
        }
        else {
            try {
                senderAccount.withdraw(transaction.getAmount());
            } catch (ArithmeticException e) {
                accepted = false;
            }}
        //log.info("Inserting transaction {}: ", transaction);
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_TRANSACTION)) {
            preparedStatement.setTimestamp(1, new Timestamp(transaction.getTimestamp().getMillis()));
            preparedStatement.setString(2, transaction.getAmount().toString());
            if (Objects.equals(transaction.getType(), "DEPOSIT")) {
                preparedStatement.setNull(3, java.sql.Types.VARCHAR);
            }
            else {
                preparedStatement.setString(3, transaction.getFrom());
            }
            preparedStatement.setString(4, transaction.getId());
            if (Objects.equals(transaction.getType(), "PAYMENT")) {
                preparedStatement.setNull(5, Types.VARCHAR);
                preparedStatement.setString(6, transaction.getTo());
            }
            else {
                preparedStatement.setString(5, transaction.getTo());
                preparedStatement.setNull(6, Types.VARCHAR);
            }
            preparedStatement.setString(7, transaction.getType());
            preparedStatement.setBoolean(8, accepted);
            preparedStatement.executeUpdate();
        }

        if (accepted) {
            updateAccountBalance(connection, senderAccount);
        }

    }

    public Account fetchAccount(Connection connection, String accountID) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT Balance, Name, RoundUpEnabled FROM Accounts WHERE AccountID = ?")) {
            preparedStatement.setString(1, accountID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                BigDecimal balance = resultSet.getBigDecimal("Balance");
                String name = resultSet.getString("Name");
                boolean roundUpEnabled = resultSet.getBoolean("RoundUpEnabled");
                return new Account(accountID, name, balance, roundUpEnabled);
            }
        }
        return null; // if account was not found
    }

    private void updateAccountBalance(Connection connection, Account account) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Accounts SET Balance = ? WHERE AccountID = ?")) {
            preparedStatement.setBigDecimal(1, account.getBalance());
            preparedStatement.setString(2, account.getAccountID());
            preparedStatement.executeUpdate();
        }
    }

    // Insert account into database
    public void insertAccount(Connection connection, Account account) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_ACCOUNT)) {
            preparedStatement.setString(1, account.getAccountID());
            preparedStatement.setBigDecimal(2, account.getBalance());
            preparedStatement.setString(3, account.getName());
            preparedStatement.setBoolean(4, account.isRoundUpEnabled());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            log.info("Insert Account Failed: {}", e.getMessage());
        }
    }

    // Insert business into database
    public void insertBusiness(Connection connection, Business business) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_BUSINESS)) {
            preparedStatement.setString(1, business.getID());
            preparedStatement.setString(2, business.getName());
            preparedStatement.setString(3, business.getCategory());
            preparedStatement.setBoolean(4, business.isSanctioned());
            preparedStatement.executeUpdate();
        }
    }
}
