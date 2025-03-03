package uk.co.asepstrath.bank.services.login;

import io.jooby.Context;
import io.jooby.Session;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.services.BaseService;

import static uk.co.asepstrath.bank.Constants.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * The login process service
 */
public class ProcessLoginService extends BaseService {

    public ProcessLoginService(DataSource dataSource, Logger logger) {
        super(dataSource, logger);
    }

    /**
     * Processes user login
     * @param ctx Session context
     * Redirects to the "/account" endpoint on success
     */
    public void processLogin(Context ctx) {
        Map<String, Object> model = createModel();

        // Check if form value exists and is not empty
        String formID = getFormValue(ctx, "accountid");
        if (formID == null || formID.trim().isEmpty()) {
            addMessageToSession(ctx, SESSION_ERROR_MESSAGE, "Account ID cannot be empty.");
            redirect(ctx, ROUTE_LOGIN);
            return;
        }

        try (Connection con = getConnection();
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
                    session.put(SESSION_ACCOUNT_ID, account.getAccountID());
                    session.put(SESSION_ACCOUNT_NAME, account.getName());
                    redirect(ctx, ROUTE_ACCOUNT);
                    return;
                }

                // Handle case where account not found
                addMessageToSession(ctx, SESSION_ERROR_MESSAGE, "Invalid account ID.");
                redirect(ctx, ROUTE_LOGIN);
            }
        }
        catch (SQLException e) {
            logger.error("Database error: {}", e.getMessage(), e);
            addMessageToSession(ctx, SESSION_ERROR_MESSAGE, "A database error occurred. Please try again.");
            redirect(ctx, ROUTE_LOGIN);
        }
    }
}