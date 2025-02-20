package uk.co.asepstrath.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
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


    public DatabaseHandler(DataSource dataSource) {
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

}
