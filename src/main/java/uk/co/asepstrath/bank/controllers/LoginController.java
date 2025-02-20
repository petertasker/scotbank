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
    public ModelAndView displayLogin() {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView("login_user.hbs", model);
    }

    @POST
    @Path("/process")
    public ModelAndView processLogin(Context ctx) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Accounts WHERE AccountID=?");
             ResultSet rs = ps.executeQuery()) {

            String formID = ctx.form("accountid").value();
            ps.setString(1, formID);
            logger.info("Processing account login");

            Account account = null;
            while (rs.next()) {
                logger.info("Found account ");
                account = new Account(
                        rs.getString("AccountID"),
                        rs.getString("Name"),
                        rs.getBigDecimal("Balance"),
                        rs.getBoolean("RoundUpEnabled")
                );
            }

            Map<String, Object> model = new HashMap<>();

            // Failed to log in
            if (account == null) {
                model.put("message", "Account not found");
                return new ModelAndView("login_user.hbs", model);
            }

            contextManager.addAccountDetailsToContext(account, ctx);
            ctx.sendRedirect("/account");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
