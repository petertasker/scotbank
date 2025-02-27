package uk.co.asepstrath.bank.services.account;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import org.slf4j.Logger;

import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.DatabaseHandler;
import uk.co.asepstrath.bank.services.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

import static uk.co.asepstrath.bank.Constants.*;

public class Deposit extends Service {

    private final DatabaseHandler databaseHandler;

    public Deposit(DataSource datasource, Logger logger) {
        super(datasource, logger);
        databaseHandler = new DatabaseHandler();
    }

    public ModelAndView<Map<String, Object>> displayDeposit(Context ctx) {
        Map<String, Object> model = createModel();
        try {
            String accountId = getAccountIdFromSession(ctx);
            putBalanceInModel(model, accountId);
            return render(URL_PAGE_ACCOUNT_DEPOSIT, model);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            addErrorMessage(model, "Error while loading deposit page.");
            return render(URL_PAGE_ACCOUNT_DEPOSIT, model);
        }
    }

    public ModelAndView<Map<String, Object>> depositProcess(Context ctx) throws SQLException {
        Map<String, Object> model = createModel();
        String accountId = getAccountIdFromSession(ctx);
        try (Connection connection = getConnection()) {
            BigDecimal amount = getFormBigDecimal(ctx, "depositamount");
            Account account = databaseHandler.fetchAccount(connection, accountId);
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
