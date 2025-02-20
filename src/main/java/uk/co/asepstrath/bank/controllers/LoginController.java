package uk.co.asepstrath.bank.controllers;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.annotation.GET;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.ContextManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Path("/login")
public class LoginController {


    private final DataSource dataSource;
    private final Logger logger;
    ContextManager contextManager = new ContextManager();

    public LoginController(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;
        logger.info("Login Controller initialised");
    }

    @GET
    public ModelAndView<Map<String, Object>> displayLogin() {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView<>(Constants.URL_PAGE_LOGIN, model);
    }

    @POST
    @Path("/process")
    public ModelAndView<Map<String, Object>> processLogin(Context ctx) {
        Map<String, Object> model = new HashMap<>();

        // Check if form value exists and is not empty
        String formID = ctx.form("accountid").valueOrNull();
        if (formID == null || formID.trim().isEmpty()) {
            model.put(Constants.URL_ERROR, "Account ID is required");
            return new ModelAndView<>(Constants.URL_PAGE_LOGIN, model);
        }

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT AccountID, Name, Balance, RoundUpEnabled FROM Accounts WHERE AccountID=?")) {

            ps.setString(1, formID);

            try (ResultSet rs = ps.executeQuery()) {
                logger.info("Processing account login");

                Account account = null;
                if (rs.next()) {  // Changed while to if since we expect one result
                    logger.info("Found account");
                    account = new Account(
                            rs.getString("AccountID"),
                            rs.getString("Name"),
                            rs.getBigDecimal("Balance"),
                            rs.getBoolean("RoundUpEnabled")
                    );
                    // Add to model
                    contextManager.addAccountDetailsToContext(account, ctx);

                    // Get details from context manager (pedantic but is good form)
                    model.put(Constants.URL_ACCOUNT_ID, contextManager.getAccountIdFromContext(ctx));
                    model.put(Constants.URL_ACCOUNT_NAME, contextManager.getNameFromContext(ctx));

                    String URL_PAGE_ACCOUNT = "account.hbs";
                    return new ModelAndView<>(Constants.URL_PAGE_ACCOUNT, model);
                }
                else {
                    // Handle case where account not found
                    model.put(Constants.URL_ERROR, "Account not found");
                    return new ModelAndView<>(Constants.URL_PAGE_LOGIN, model);
                }
            }
        }
        catch (SQLException e) {
            logger.error("Database error: {}", e.getMessage(), e);
            model.put(Constants.URL_ERROR, "Database error occurred");
            return new ModelAndView<>(Constants.URL_PAGE_LOGIN, model);
        }
    }

}
