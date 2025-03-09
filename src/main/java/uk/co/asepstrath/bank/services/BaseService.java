package uk.co.asepstrath.bank.services;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.StatusCode;
import io.jooby.exception.StatusCodeException;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import java.util.Locale;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.text.DecimalFormat;
import static uk.co.asepstrath.bank.Constants.*;

public abstract class BaseService implements
        FormBigDecimalRetriever,
        FormValueRetriever,
        ViewRenderer,
        CurrencyFormatter,
        SessionMessageManager
{

    private final DataSource dataSource;
    protected final Logger logger;

    protected BaseService(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;
    }

    protected BaseService(Logger logger) {
        this.logger = logger;
        this.dataSource = null;
    }

    @Override
    public String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "£0.00"; // Default value if balance is null
        }

        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.UK);
        DecimalFormat decimalFormat = (DecimalFormat) formatter;
        decimalFormat.applyPattern("#,###.00");

        return decimalFormat.format(amount);
    }


    /**
     * Renders an endpoint
     *
     * @param viewName The handlebars file to be rendered
     * @param model    The map of which is modelled onto the view
     * @return an endpoint
     */
    @Override
    public ModelAndView<Map<String, Object>> render(String viewName, Map<String, Object> model) {
        return new ModelAndView<>(viewName, model);
    }

    /**
     * Creates a model
     *
     * @return a Map
     */
    protected Map<String, Object> createModel() {
        return new HashMap<>();
    }


    @Override
    public void addMessageToSession(Context ctx, String key, String message) {
        Session session = ctx.session();
        session.put(key, message);
    }

    @Override
    public void transferSessionAttributeToModel(Context ctx, String attributeName, Map<String, Object> model) {
        Session session = ctx.session();
        model.put(attributeName, session.get(attributeName));
        logger.info("Removing session attribute: {}, {} ", attributeName, session.get(attributeName));
        session.remove(attributeName);
        logger.info("Removed session attribute: {}, {}", attributeName, model.get(attributeName));

    }


    /**
     * Gets connection to the database
     *
     * @return Database connection
     * @throws SQLException Database connection failure
     */
    protected Connection getConnection() throws SQLException {
        assert dataSource != null;
        return dataSource.getConnection();
    }

    /**
     * Gets session Object
     *
     * @param ctx Session context
     * @return Session Object
     */
    protected Session getSession(Context ctx) {
        return ctx.session();
    }

    /**
     * Redirects the user, usually used when succeeding a process
     *
     * @param ctx Session context
     * @param url an endpoint
     */
    protected void redirect(Context ctx, String url) {
        ctx.sendRedirect(url);
    }

    /**
     * Gets the value from a context form
     *
     * @param ctx  Session context
     * @param name the name of the form input
     * @return String value of the form input
     */
    @Override
    public String getFormValue(Context ctx, String name) {
        return ctx.form(name).valueOrNull();
    }

    /**
     * Gets a BigDecimal from a context form
     *
     * @param ctx  Session context
     * @param name the name of the form input
     * @return BigDecimal value of the form input
     */
    @Override
    public BigDecimal getFormBigDecimal(Context ctx, String name) {
        String value = Objects.requireNonNull(ctx.form(name).valueOrNull()).trim();
        return new BigDecimal(value);
    }
}
