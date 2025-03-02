package uk.co.asepstrath.bank.services.account;

import io.jooby.Context;
import io.jooby.ModelAndView;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.services.BaseService;
import uk.co.asepstrath.bank.services.repository.AccountRepository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static uk.co.asepstrath.bank.Constants.*;

/**
 * The Account withdrawal service
 */
public class AccountWithdrawService extends BaseService {

    private static AccountRepository accountRepository;

    public AccountWithdrawService(DataSource datasource, Logger logger) {
        super(datasource, logger);
        accountRepository = new AccountRepository(logger);
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
        return render(URL_PAGE_ACCOUNT_WITHDRAW, model);
    }

    /**
     * The withdrawal process
     * @param ctx Session Context
     * @return The "/account/withdraw" endpoint on failure
     * Redirects to "/account" on success
     * @throws SQLException Database connection error
     * @throws ArithmeticException User input error
     */
    public ModelAndView<Map<String, Object>> withdrawProcess(Context ctx) throws SQLException {
        Map<String, Object> model = createModel();
        try (Connection connection = getConnection()) {
            String accountId = getAccountIdFromSession(ctx);
            BigDecimal amount = getFormBigDecimal(ctx, "withdrawalamount");
            Account account = accountRepository.getAccount(connection, accountId);
            try {
                account.withdraw(amount);
                updateDatabaseBalance(account);
                redirect(ctx, "/account");
                return null;
            } catch (ArithmeticException e) {
                logger.error(e.getMessage());
                addErrorMessage(model, "Error while withdrawing amount");
                putBalanceInModel(model, accountId);
                return render(URL_PAGE_ACCOUNT_DEPOSIT, model);
            }
        }
    }
}
