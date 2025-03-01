package uk.co.asepstrath.bank.services.repository;

import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.Transaction;

import java.sql.*;
import java.util.Objects;

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


    private final AccountRepository accountRepository;

    public TransactionRepository(Logger logger, AccountRepository accountRepository) {
        super(logger);
        this.accountRepository = accountRepository;
    }

    public void createTable(Connection connection) throws SQLException {
        executeUpdate(connection, SQL_CREATE_TABLE);
    }

    public void insert(Connection connection, Transaction transaction) throws SQLException {
        boolean accepted = processTransaction(connection, transaction);

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
            stmt.setBoolean(8, accepted);
            stmt.executeUpdate();
        }
    }

    private boolean processTransaction(Connection connection, Transaction transaction) throws SQLException {
        if (!Objects.equals(transaction.getType(), "DEPOSIT")) {
            Account senderAccount = accountRepository.getAccount(connection, transaction.getFrom());
            if (senderAccount == null) {
                return false;
            }

            try {
                senderAccount.withdraw(transaction.getAmount());
                accountRepository.updateBalance(connection, senderAccount);
                return true;
            } catch (ArithmeticException e) {
                return false;
            }
        }
        return true;
    }
}
