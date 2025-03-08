package uk.co.asepstrath.bank.services.account;

import io.jooby.Context;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.Constants;
import uk.co.asepstrath.bank.Transaction;
import uk.co.asepstrath.bank.services.BaseService;
import uk.co.asepstrath.bank.services.repository.TransactionRepository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static uk.co.asepstrath.bank.Constants.ROUTE_ACCOUNT;
import static uk.co.asepstrath.bank.Constants.SESSION_SUCCESS_MESSAGE;

public class AccountService extends BaseService {

    private final TransactionRepository transactionRepository;

    public AccountService(DataSource dataSource, Logger logger) {
        super(dataSource, logger);
        transactionRepository = new TransactionRepository(logger);
    }

    /**
     * Updates the balance of an Account on the database end
     *
     * @param account an Account Object
     * @throws SQLException Database connection failure
     */
    void updateDatabaseBalance(Account account) throws SQLException {

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE Accounts SET Balance = ? WHERE AccountID = ?")) {
            statement.setBigDecimal(1, account.getBalance());
            statement.setString(2, account.getAccountID());
            statement.executeUpdate();
        }
    }


    void executeTransaction(Context ctx, Connection connection, Transaction transaction) throws SQLException {
        try {
            connection.setAutoCommit(false); // Start transaction
            transactionRepository.insert(connection, transaction);
        } catch (ArithmeticException e) {
            addMessageToSession(ctx, Constants.SESSION_ERROR_MESSAGE, e.getMessage());
            logger.info("Transaction blocked due to potential balance overflow");
            connection.setAutoCommit(true);
            redirect(ctx, ROUTE_ACCOUNT);
        } finally {
            connection.setAutoCommit(true);
        }
    }

    void executeDeposit(Context ctx, Account account, BigDecimal amount) throws SQLException {
        try {
            account.deposit(amount);
            updateDatabaseBalance(account);
            logger.info("Successfully deposited into account");
            addMessageToSession(ctx, SESSION_SUCCESS_MESSAGE, "Successfully deposited into account!");
        } catch (ArithmeticException e) {
            logger.info("Unable to deposit into account");
            addMessageToSession(ctx, Constants.SESSION_ERROR_MESSAGE, e.getMessage());
        } finally {
            redirect(ctx, ROUTE_ACCOUNT);
        }
    }

    void executeWithdrawal(Context ctx, Account account, BigDecimal amount) throws SQLException {
        try {
            account.withdraw(amount);
            updateDatabaseBalance(account);
            logger.info("Successfully withdrawn from account");
            addMessageToSession(ctx, SESSION_SUCCESS_MESSAGE, "Successfully withdrawn from account!");
        } catch (ArithmeticException e) {
            addMessageToSession(ctx, Constants.SESSION_ERROR_MESSAGE, "Transaction failed: " + e.getMessage());
            logger.error("Transaction failed", e);
        } finally {
            redirect(ctx, ROUTE_ACCOUNT);
        }
    }

}
