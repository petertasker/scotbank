package uk.co.asepstrath.bank.services;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static uk.co.asepstrath.bank.Constants.SESSION_ACCOUNT_ID;
import static uk.co.asepstrath.bank.Constants.URL_ERROR_MESSAGE;

public abstract class BaseService {

    protected final DataSource dataSource;
    protected final Logger logger;

    protected BaseService(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;
    }

    protected BaseService(Logger logger) {
        this.logger = logger;
        this.dataSource = null;
    }

    protected ModelAndView<Map<String, Object>> render(String viewName, Map<String, Object> model) {
        return new ModelAndView<>(viewName, model);
    }

    protected  Map<String, Object> createModel() {
        return new HashMap<>();
    }

    protected void addErrorMessage(Map<String, Object> model, String message) {
        model.put(URL_ERROR_MESSAGE, message);
    }

    protected Connection getConnection() throws SQLException {
        assert dataSource != null;
        return dataSource.getConnection();
    }

    protected Session getSession(Context ctx) {
        return ctx.session();
    }

    protected String getFormValue(Context ctx, String name) {
        return ctx.form(name).valueOrNull();
    }

    public BigDecimal getFormBigDecimal(Context ctx, String name) {
        return BigDecimal.valueOf(Double.parseDouble(Objects.requireNonNull(ctx.form(name).valueOrNull())));
    }

    protected void redirect(Context ctx, String url) {
        ctx.sendRedirect(url);
    }

    protected void putBalanceInModel(Map<String, Object> model, String accountId) {
        BigDecimal balance = BigDecimal.ZERO;
        try(PreparedStatement statement = getConnection().prepareStatement("select Balance from Accounts where AccountID = ?")) {
            statement.setString(1, accountId);
            ResultSet rs = statement.executeQuery();
            try(rs){
                if (rs.next()) {
                    balance = rs.getBigDecimal("Balance");
                    logger.info("Account balance: {}" , balance);
                }else{
                    logger.info("Account balance is empty");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        model.put("balance", balance);
    }

    public void updateDatabaseBalance(Account account) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE Accounts SET Balance = ? WHERE AccountID = ?")) {

            statement.setBigDecimal(1, account.getBalance());
            statement.setString(2, account.getAccountID());
            statement.executeUpdate();
        }
    }

    protected String getAccountIdFromSession(Context ctx) {
        Session session = getSession(ctx);
        return String.valueOf(session.get(SESSION_ACCOUNT_ID));
    }

}
