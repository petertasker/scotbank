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
        return new ModelAndView<>("login_user.hbs", model);
    }

    @POST
    @Path("/process")
    public ModelAndView<Map<String, Object>> processLogin(Context ctx) {
        Map<String, Object> model = new HashMap<>();

        // Check if form value exists and is not empty
        String formID = ctx.form("accountid").valueOrNull();
        if (formID == null || formID.trim().isEmpty()) {
            model.put("error", "Account ID is required");
            return new ModelAndView<>("login.hbs", model);
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
                    model.put("accountid", contextManager.getAccountIdFromContext(ctx));
                    model.put("name", contextManager.getNameFromContext(ctx));

                    return new ModelAndView<>("account.hbs", model);
                }
                else {
                    // Handle case where account not found
                    model.put("error", "Account not found");
                    return new ModelAndView<>("login_user.hbs", model);
                }
            }
        }
        catch (SQLException e) {
            logger.error("Database error: " + e.getMessage(), e);
            model.put("error", "Database error occurred");
            return new ModelAndView<>("login_user.hbs", model);
        }
    }

}
