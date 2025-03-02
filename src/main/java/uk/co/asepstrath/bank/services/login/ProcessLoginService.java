package uk.co.asepstrath.bank.services.login;

import io.jooby.Context;
import io.jooby.ModelAndView;
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
     * @return The "/login" endpoint on failure
     * Redirects to the "/account" endpoint on success
     */
    public ModelAndView<Map<String, Object>> processLogin(Context ctx) {
        Map<String, Object> model = createModel();

        // Check if form value exists and is not empty
        String formID = getFormValue(ctx, "accountid");
        if (formID == null || formID.trim().isEmpty()) {
            addErrorMessage(model, "Account ID is required");
            return render(TEMPLATE_LOGIN, model);
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
                    return null;
                }

                // Handle case where account not found
                addErrorMessage(model, "Account not found");
                return render(TEMPLATE_LOGIN, model);

            }
        }
        catch (SQLException e) {
            logger.error("Database error: {}", e.getMessage(), e);
            addErrorMessage(model, "Database error!");
            return render(TEMPLATE_LOGIN, model);
        }
    }
}