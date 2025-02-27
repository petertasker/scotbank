package uk.co.asepstrath.bank.services.account;

import io.jooby.Context;
import io.jooby.ModelAndView;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.DatabaseHandler;
import uk.co.asepstrath.bank.services.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static uk.co.asepstrath.bank.Constants.*;

public class ServiceAccountWithdraw extends Service {

    private static final DatabaseHandler databaseHandler = new DatabaseHandler();

    public ServiceAccountWithdraw(DataSource datasource, Logger logger) {
        super(datasource, logger);
    }

    public ModelAndView<Map<String, Object>> renderWithdraw(Context ctx) {
        Map<String, Object> model = createModel();
        String accountId = getAccountIdFromSession(ctx);
        putBalanceInModel(model, accountId);
        return render(URL_PAGE_ACCOUNT_WITHDRAW, model);
    }

    public ModelAndView<Map<String, Object>> withdrawProcess(Context ctx) throws SQLException {
        Map<String, Object> model = createModel();
        try (Connection connection = getConnection()) {
            String accountId = getAccountIdFromSession(ctx);
            BigDecimal amount = getFormBigDecimal(ctx, "withdrawalamount");
            Account account = databaseHandler.fetchAccount(connection, accountId);
            try {
                account.withdraw(amount);
                updateDatabaseBalance(account);
                redirect(ctx, "/account");
                return null;
            } catch (ArithmeticException e) {
                logger.error(e.getMessage());
                addErrorMessage(model, "Error while withdrawing amount");
                putBalanceInModel(model, accountId);
                return render("/account/withdraw", model);
            }
        }
    }
}
