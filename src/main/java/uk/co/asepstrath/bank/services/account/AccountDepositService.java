package uk.co.asepstrath.bank.services.account;

import io.jooby.Context;
import io.jooby.ModelAndView;
import org.joda.time.DateTime;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.Constants;
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
public class AccountDepositService extends AccountService {

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
        transferSessionAttributeToModel(ctx, Constants.SESSION_ERROR_MESSAGE, model);
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
            executeTransaction(ctx, connection, transaction);
            executeDeposit(ctx, account, amount);
        }
    }
}
