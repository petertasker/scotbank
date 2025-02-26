package uk.co.asepstrath.bank.services.account;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import org.slf4j.Logger;

import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.DatabaseHandler;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

import static uk.co.asepstrath.bank.Constants.*;

public class Deposit {
    private final Logger logger;
    private final DataSource dataSource;
    private final DatabaseHandler databaseHandler;
    private final ReuseServices reuseServices;

    public Deposit(DataSource datasource, Logger logger){
        this.dataSource = datasource;
        this.logger = logger;
        this.databaseHandler = new DatabaseHandler();
        this.reuseServices = new ReuseServices(datasource, logger);
        logger.info("Deposit Service initialised");
    }
    
    public ModelAndView<Map<String, Object>> displayDeposit(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        Session session = ctx.session();

        reuseServices.putBalanceInModel(model, String.valueOf(session.get(SESSION_ACCOUNT_ID)));
        return new ModelAndView<>(URL_PAGE_ACCOUNT_DEPOSIT, model);
    }

    public ModelAndView<Map<String, Object>> depositProcess(Context ctx) throws SQLException {
       try (Connection connection = dataSource.getConnection()) {
           Session session = ctx.session();
           String accountId = String.valueOf(session.get(SESSION_ACCOUNT_ID));
           BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(ctx.form("depositamount").value()));
           Account account = databaseHandler.fetchAccount(connection, accountId);
           Map<String, Object> model = new HashMap<>();
           try {
               account.deposit(amount);
               reuseServices.updateDatabaseBalance(account);
               ctx.sendRedirect("/account");
           } catch (ArithmeticException e) {
               logger.error(e.getMessage());
               model.put(URL_ERROR_MESSAGE, e.getMessage());
               reuseServices.putBalanceInModel(model,accountId);
               return new ModelAndView<>(URL_PAGE_ACCOUNT_DEPOSIT,model);
           }
       }
       return null;
    }
}
