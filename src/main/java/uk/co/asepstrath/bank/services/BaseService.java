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

    /**
     * Renders an endpoint
     * @param viewName The handlebars file to be rendered
     * @param model The map of which is modelled onto the view
     * @return an endpoint
     */
    protected ModelAndView<Map<String, Object>> render(String viewName, Map<String, Object> model) {
        return new ModelAndView<>(viewName, model);
    }

    /**
     * Creates a model
     * @return a Map
     */
    protected  Map<String, Object> createModel() {
        return new HashMap<>();
    }

    /**
     * Adds an error message to a model
     * @param model A map
     * @param message A custom error message
     */
    protected void addErrorMessage(Map<String, Object> model, String message) {
        model.put(URL_ERROR_MESSAGE, message);
    }

    /**
     * Gets connection to the database
     * @return Database connection
     * @throws SQLException Database connection failure
     */
    protected Connection getConnection() throws SQLException {
        assert dataSource != null;
        return dataSource.getConnection();
    }

    /**
     * Gets session Object
     * @param ctx Session context
     * @return Session Object
     */
    protected Session getSession(Context ctx) {
        return ctx.session();
    }

    /**
     * Gets the value from a context form
     * @param ctx Session context
     * @param name the name of the form input
     * @return String value of the form input
     */
    protected String getFormValue(Context ctx, String name) {
        return ctx.form(name).valueOrNull();
    }

    /**
     * Gets a BigDecimal from a context form
     * @param ctx Session context
     * @param name the name of the form input
     * @return BigDecimal value of the form input
     */
    public BigDecimal getFormBigDecimal(Context ctx, String name) {
        String value = Objects.requireNonNull(ctx.form(name).valueOrNull()).trim();
        return new BigDecimal(value);
    }


    /**
     * Redirects the user, usually used when succeeding a process
     * @param ctx Session context
     * @param url an endpoint
     */
    protected void redirect(Context ctx, String url) {
        ctx.sendRedirect(url);
    }

    /**
     * Puts the balance of an Account Object in the model to be rendered
     * @param model A Map
     * @param accountId The unique identifier of the Account Object
     */
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

    /**
     * Updates the balance of an Account on the database end
     * @param account an Account Object
     * @throws SQLException Database connection failure
     */
    public void updateDatabaseBalance(Account account) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE Accounts SET Balance = ? WHERE AccountID = ?")) {

            statement.setBigDecimal(1, account.getBalance());
            statement.setString(2, account.getAccountID());
            statement.executeUpdate();
        }
    }

    /**
     * Gets the Account Object unique identifier from the session
     * @param ctx Session context
     * @return Session accountId
     */
    protected String getAccountIdFromSession(Context ctx) {
        Session session = getSession(ctx);
        return String.valueOf(session.get(SESSION_ACCOUNT_ID));
    }

}
