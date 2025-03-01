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

public class AccountDepositService extends BaseService {

    private static AccountRepository accountRepository;

    public AccountDepositService(DataSource datasource, Logger logger) {
        super(datasource, logger);
        accountRepository = new AccountRepository(logger);
    }

    public ModelAndView<Map<String, Object>> renderDeposit(Context ctx) {
        Map<String, Object> model = createModel();
        String accountId = getAccountIdFromSession(ctx);
        putBalanceInModel(model, accountId);
        return render(URL_PAGE_ACCOUNT_DEPOSIT, model);
    }

    public ModelAndView<Map<String, Object>> processDeposit(Context ctx) throws SQLException {
        Map<String, Object> model = createModel();
        String accountId = getAccountIdFromSession(ctx);
        try (Connection connection = getConnection()) {
            BigDecimal amount = getFormBigDecimal(ctx, "depositamount");
            Account account = accountRepository.getAccount(connection, accountId);
            try {
                account.deposit(amount);
                updateDatabaseBalance(account);
                redirect(ctx, "/account");
                return null;
            } catch (ArithmeticException e) {
                logger.error(e.getMessage());
                addErrorMessage(model, "Error while depositing amount.");
                putBalanceInModel(model, accountId);
                return render(URL_PAGE_ACCOUNT_DEPOSIT, model);
            }
        }
    }
}
