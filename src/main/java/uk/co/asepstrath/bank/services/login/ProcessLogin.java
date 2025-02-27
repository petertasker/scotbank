package uk.co.asepstrath.bank.services.login;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;

import static uk.co.asepstrath.bank.Constants.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ProcessLogin {

    private final DataSource dataSource;
    private final Logger logger;

    public ProcessLogin(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;
        logger.info("Process Login Handler initialised");
    }

    public ModelAndView<Map<String, Object>> processLogin(Context ctx) {
        Map<String, Object> model = new HashMap<>();

        // Check if form value exists and is not empty
        String formID = ctx.form("accountid").valueOrNull();
        if (formID == null || formID.trim().isEmpty()) {
            model.put(URL_ERROR_MESSAGE, "Account ID is required");
            return new ModelAndView<>(URL_PAGE_LOGIN, model);
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

                    // Add accountID to session
                    Session session = ctx.session();
                    session.put("accountid", account.getAccountID());
                    session.put("name", account.getName());
                    ctx.sendRedirect("/account");
                    return null;
                }
                else {
                    // Handle case where account not found
                    model.put(URL_ERROR_MESSAGE, "Account not found");
                    return new ModelAndView<>(URL_PAGE_LOGIN, model);
                }
            }
        }
        catch (SQLException e) {
            logger.error("Database error: {}", e.getMessage(), e);
            model.put(URL_ERROR_MESSAGE, "Database error occurred");
            return new ModelAndView<>(URL_PAGE_LOGIN, model);
        }
    }
}