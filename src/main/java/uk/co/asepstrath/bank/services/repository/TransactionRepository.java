package uk.co.asepstrath.bank.services.repository;

import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.Transaction;

import java.sql.*;
import java.util.Objects;

/**
 * The Transaction repository service
 */
public class TransactionRepository extends BaseRepository {

    private static final String SQL_CREATE_TABLE = """
    CREATE TABLE Transactions (
        Timestamp DATETIME NOT NULL,
        Amount DECIMAL(12,2) NOT NULL,
        SenderID VARCHAR(255) NULL,
        TransactionID VARCHAR(255) NOT NULL,
        ReceiverAccountID VARCHAR(255) NULL,
        ReceiverBusinessID VARCHAR(255) NULL,
        TransactionType VARCHAR(255) NOT NULL,
        TransactionAccepted BIT NOT NULL,
        PRIMARY KEY (TransactionID),
        FOREIGN KEY (ReceiverAccountID) REFERENCES Accounts(AccountID),
        FOREIGN KEY (ReceiverBusinessID) REFERENCES Businesses(BusinessID),
        FOREIGN KEY (SenderID) REFERENCES Accounts(AccountID),
        CONSTRAINT CHK_Receiver CHECK (ReceiverAccountID IS NOT NULL OR ReceiverBusinessID IS NOT NULL OR SenderID IS NOT NULL)
    )
    """;

    private static final String SQL_INSERT_TRANSACTION =
            "INSERT INTO Transactions (Timestamp, Amount, SenderID, TransactionID, ReceiverAccountID, ReceiverBusinessID, TransactionType, TransactionAccepted)\n" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";


    public TransactionRepository(Logger logger) {
        super(logger);
    }

    /**
     * Creates the Transaction table
     * @param connection Database connection
     * @throws SQLException Database connection failure
     */
    public void createTable(Connection connection) throws SQLException {
        executeUpdate(connection, SQL_CREATE_TABLE);
    }

    /**
     * Inserts a Transaction into the Transaction table
     * @param connection Database Connection
     * @param transaction a Transaction object
     * @throws SQLException Database connection failure
     */
    public void insert(Connection connection, Transaction transaction) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_INSERT_TRANSACTION)) {
            stmt.setTimestamp(1, new Timestamp(transaction.getTimestamp().getMillis()));
            stmt.setString(2, transaction.getAmount().toString());

            if (Objects.equals(transaction.getType(), "DEPOSIT")) {
                stmt.setNull(3, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(3, transaction.getFrom());
            }

            stmt.setString(4, transaction.getId());

            if (Objects.equals(transaction.getType(), "PAYMENT")) {
                stmt.setNull(5, Types.VARCHAR);
                stmt.setString(6, transaction.getTo());
            } else {
                stmt.setString(5, transaction.getTo());
                stmt.setNull(6, Types.VARCHAR);
            }

            stmt.setString(7, transaction.getType());
            stmt.setBoolean(8, transaction.getStatus());
            stmt.executeUpdate();
        }
    }
}
