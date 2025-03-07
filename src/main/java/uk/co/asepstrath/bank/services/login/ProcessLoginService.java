package uk.co.asepstrath.bank.services.login;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.StatusCode;
import io.jooby.exception.StatusCodeException;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.Constants;
import uk.co.asepstrath.bank.services.BaseService;

import static uk.co.asepstrath.bank.Constants.*;

import javax.sql.DataSource;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    public void processLogin(Context ctx) throws SQLException {
        // Check if form value exists and is not empty
        String formID = getFormValue(ctx, "accountid");
        String password = getFormValue(ctx, "password");

        if (formID == null || formID.trim().isEmpty()) {
            addMessageToSession(ctx, Constants.SESSION_ERROR_MESSAGE, "Account ID cannot be empty.");
            redirect(ctx, ROUTE_LOGIN);
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            addMessageToSession(ctx,SESSION_ERROR_MESSAGE, "Password must not br empty");
            redirect(ctx, ROUTE_LOGIN);
            return;
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT AccountID, Password, Name, Balance, RoundUpEnabled FROM Accounts WHERE AccountID=?")) {

            ps.setString(1, formID);

            try (ResultSet rs = ps.executeQuery()) {
                logger.info("Processing account login");

                Account account = null;
                if (rs.next()) {  // Changed while to if since we expect one result
                    String hashedPassword = rs.getString("Password");
                    if (HashingPasswordService.verifyPassword(password, hashedPassword)) {
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
                        return; // Added return to exit the method
                    }
                }

                // If we get here, either no account was found or password didn't match
                addMessageToSession(ctx, SESSION_ERROR_MESSAGE, "Incorrect username or password.");
                redirect(ctx, ROUTE_LOGIN);
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error("Database error: {}", e.getMessage(), e);
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Failed to reach database");
        }
    }
}