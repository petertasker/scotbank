package uk.co.asepstrath.bank.services.manager;

import io.jooby.ModelAndView;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.DataAccessException;
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

    private final ManagerRepository managerRepository;

    public ViewManagerDashboardService(DataSource datasource, Logger logger) {
        super(datasource, logger);
        managerRepository = new ManagerRepository(logger);
    }


    /**
     * Renders the manager's dashboard
     * @return The "/manager/dashboard" endpoint
     */
    public ModelAndView<Map<String, Object>> renderDashboard() {
        List<Account> accounts;
        Map<String, Object> model = createModel();
        try {
            accounts = managerRepository.getAllAccounts(getConnection());
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve accounts", e);
        }

        model.put(ACCOUNT_OBJECT_LIST_EXISTS, !accounts.isEmpty());
        model.put(ACCOUNT_OBJECT_LIST, accounts);
        return render(URL_PAGE_MANAGER_DASHBOARD, model);
    }
}
