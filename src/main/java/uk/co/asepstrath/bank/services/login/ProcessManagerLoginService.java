package uk.co.asepstrath.bank.services.login;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.StatusCode;
import io.jooby.exception.StatusCodeException;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Manager;
import uk.co.asepstrath.bank.services.BaseService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static uk.co.asepstrath.bank.Constants.*;

/**
 * The manager login process service
 */
public class ProcessManagerLoginService extends BaseService {
    public ProcessManagerLoginService(DataSource dataSource, Logger logger) {
        super(dataSource, logger);
    }

    /**
     * Processes the manager login
     * @param ctx Session context
     * @return the "/manager/login" endpoint on failure
     * Redirects to "/manager/dashboard" on success
     */
    public ModelAndView<Map<String, Object>> processManagerLogin(Context ctx) {
        // Validate manager ID
        String formManagerID = getFormValue(ctx, "managerid");
        if (formManagerID == null || formManagerID.trim().isEmpty()) {
            addMessageToSession(ctx, SESSION_ERROR_MESSAGE, "Account ID cannot be empty.");
            redirect(ctx, ROUTE_MANAGER + ROUTE_LOGIN);
            return null;
        }

        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT ManagerID, Name FROM Managers WHERE ManagerID = ?")
        ) {
            stmt.setString(1, formManagerID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Manager found - create session
                    Manager manager = new Manager(
                            rs.getString("ManagerID"),
                            rs.getString("Name")
                    );

                    createManagerSession(ctx, manager);
                    logger.info("Process Manager Login Success");
                    redirect(ctx, ROUTE_MANAGER + ROUTE_DASHBOARD);
                    return null;

                } else {
                    // Manager not found
                    handleLoginFailure(ctx, "Invalid account ID.");
                    return null;
                }
            }

        } catch (SQLException e) {
            logger.error("Database error during manager login", e);
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "A database error occurred");
        }
    }

    private void handleLoginFailure(Context ctx, String message) {
        addMessageToSession(ctx, SESSION_ERROR_MESSAGE, message);
        redirect(ctx, ROUTE_MANAGER + ROUTE_LOGIN);
    }

    private void createManagerSession(Context ctx, Manager manager) {
        Session session = ctx.session();
        session.put(SESSION_MANAGER_ID, manager.getManagerID());
        session.put(SESSION_MANAGER_NAME, manager.getName());
    }
}
