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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import static uk.co.asepstrath.bank.Constants.*;

/**
 * The Account withdrawal service
 */
public class AccountWithdrawService extends BaseService {

    private final AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    public AccountWithdrawService(DataSource datasource, Logger logger) {
        super(datasource, logger);
        accountRepository = new AccountRepository(logger);
        transactionRepository = new TransactionRepository(logger);
    }

    public AccountWithdrawService(DataSource datasource, Logger logger, AccountRepository accountRepository) {
        super(datasource, logger);
        this.accountRepository = accountRepository;
    }

    /**
     * Displays the "/account/withdraw" endpoint
     * @param ctx Session Context
     * @return The "/account/withdraw" endpoint
     */
    public ModelAndView<Map<String, Object>> renderWithdraw(Context ctx) {
        Map<String, Object> model = createModel();
        String accountId = getAccountIdFromSession(ctx);
        putBalanceInModel(model, accountId);
        transferSessionAttributeToModel(ctx, SESSION_ERROR_MESSAGE, model);
        return render(TEMPLATE_WITHDRAW, model);
    }

    /**
     * The withdrawal process
     * @param ctx Session Context
     * Redirects to "/account" on success
     * Redirects to "/deposit" on failure
     * @throws SQLException Database connection error
     * @throws ArithmeticException User input error
     */
    public void processWithdraw(Context ctx) throws SQLException {
        logger.info("Enter withdraw process");
        try (Connection connection = getConnection()) {
            String accountId = getAccountIdFromSession(ctx);
            BigDecimal amount = getFormBigDecimal(ctx, "withdrawalamount");
            Account account = accountRepository.getAccount(connection, accountId);
            Transaction transaction = new Transaction(connection, DateTime.now(), amount, accountId, UUID.randomUUID().toString(), null , "WITHDRAWAL");
            transactionRepository.insert(connection, transaction);
            try {
                account.withdraw(amount);
                updateDatabaseBalance(account);
                addMessageToSession(ctx, SESSION_SUCCESS_MESSAGE, "Successfully withdrawn from account!");
                logger.info("Successfully withdrawn from account");
                redirect(ctx, ROUTE_ACCOUNT);
            } catch (ArithmeticException e) {
                addMessageToSession(ctx, SESSION_ERROR_MESSAGE, "Error while withdrawing amount");
                logger.info("Unable to withdraw account");
                redirect(ctx, ROUTE_ACCOUNT + ROUTE_WITHDRAW);
            }
        }
    }
}
