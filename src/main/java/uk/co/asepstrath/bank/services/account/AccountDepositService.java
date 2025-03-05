package uk.co.asepstrath.bank.services.account;

import io.jooby.Context;
import io.jooby.ModelAndView;
import org.joda.time.DateTime;
import org.slf4j.Logger;

import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.Transaction;
import uk.co.asepstrath.bank.services.BaseService;
import uk.co.asepstrath.bank.services.repository.AccountRepository;
import uk.co.asepstrath.bank.services.repository.TransactionRepository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

import static uk.co.asepstrath.bank.Constants.*;

/**
 * The Account deposit service
 */
public class AccountDepositService extends BaseService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountDepositService(DataSource datasource, Logger logger) {
        super(datasource, logger);
        accountRepository = new AccountRepository(logger);
        transactionRepository = new TransactionRepository(logger);
    }

    /**
     * Displays the deposit endpoint
     * @param ctx Session context
     * @return The "/account/deposit" endpoint
     */
    public ModelAndView<Map<String, Object>> renderDeposit(Context ctx) {
        Map<String, Object> model = createModel();
        String accountId = getAccountIdFromSession(ctx);
        putBalanceInModel(model, accountId);
        transferSessionAttributeToModel(ctx, SESSION_ERROR_MESSAGE, model);
        return render(TEMPLATE_DEPOSIT, model);
    }

    /**
     * The deposit process
     *
     * @param ctx Session context
     * @throws SQLException        Database connection error
     * @throws ArithmeticException User bad input error
     */
    public void processDeposit(Context ctx) throws SQLException {
        logger.info("Enter deposit process");
        String accountId = getAccountIdFromSession(ctx);
        try (Connection connection = getConnection()) {

            BigDecimal amount = getFormBigDecimal(ctx, "depositamount");
            Account account = accountRepository.getAccount(connection, accountId);
            Transaction transaction = new Transaction(connection, DateTime.now(), amount, null, UUID.randomUUID().toString(), accountId, "DEPOSIT");

            try {
                connection.setAutoCommit(false);
                transactionRepository.insert(connection, transaction);
            } catch (ArithmeticException e) {
                addMessageToSession(ctx, SESSION_ERROR_MESSAGE, e.getMessage());
                logger.info("Transaction blocked due to potential balance overflow");
                connection.setAutoCommit(true);
                redirect(ctx, ROUTE_ACCOUNT);
            } finally {
                connection.setAutoCommit(true);
            }


            try {
                account.deposit(amount);
                updateDatabaseBalance(account);
                logger.info("Successfully deposited into account");
                addMessageToSession(ctx, SESSION_SUCCESS_MESSAGE, "Successfully deposited into account!");
            } catch (ArithmeticException e) {
                logger.info("Unable to deposit into account");
                addMessageToSession(ctx, SESSION_ERROR_MESSAGE, e.getMessage());
            } finally {
                redirect(ctx, ROUTE_ACCOUNT);
            }
        }
    }
}
