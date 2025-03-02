package uk.co.asepstrath.bank.services.manager;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.services.BaseService;
import uk.co.asepstrath.bank.services.repository.ManagerRepository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static uk.co.asepstrath.bank.Constants.*;

/**
 * The manager dashboard viewing service
 */
public class ViewManagerDashboardService extends BaseService {

    private static ManagerRepository managerRepository;

    public ViewManagerDashboardService(DataSource datasource, Logger logger) {
        super(datasource, logger);
        managerRepository = new ManagerRepository(logger);
    }

    /**
     * Renders the manager's dashboard
     * @return The "/manager/dashboard" endpoint
     */
    public ModelAndView<Map<String, Object>> renderDashboard(Context ctx) {
        List<Account> accounts;
        Map<String, Object> model = createModel();
        try {
            accounts = managerRepository.getAllAccounts(getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Session session = getSession(ctx);
        model.put(ACCOUNT_OBJECT_LIST_EXISTS, !accounts.isEmpty());
        model.put(ACCOUNT_OBJECT_LIST, accounts);
        model.put(SESSION_MANAGER_NAME, session.get(SESSION_MANAGER_NAME));
        model.put(SESSION_MANAGER_ID, session.get(SESSION_MANAGER_ID));
        return render(URL_PAGE_MANAGER_DASHBOARD, model);
    }
}
