package uk.co.asepstrath.bank.services.account;

import io.jooby.Context;
import io.jooby.ModelAndView;
import org.slf4j.Logger;

import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.services.BaseService;
import uk.co.asepstrath.bank.services.repository.AccountRepository;

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

    public AccountDepositService(DataSource datasource, Logger logger) {
        super(datasource, logger);
        accountRepository = new AccountRepository(logger);
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
        String accountId = getAccountIdFromSession(ctx);
        try (Connection connection = getConnection()) {
            BigDecimal amount = getFormBigDecimal(ctx, "depositamount");
            Account account = accountRepository.getAccount(connection, accountId);
            try {
                account.deposit(amount);
                updateDatabaseBalance(account);
                redirect(ctx, ROUTE_ACCOUNT);
            } catch (ArithmeticException e) {
                logger.error(e.getMessage());
                addMessageToSession(ctx, SESSION_ERROR_MESSAGE, "Error while depositing amount.");
                redirect(ctx, ROUTE_ACCOUNT + ROUTE_DEPOSIT);
            }
        }
    }
}
