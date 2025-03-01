package uk.co.asepstrath.bank.services.manager;

import io.jooby.ModelAndView;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.services.BaseService;
import uk.co.asepstrath.bank.services.repository.ManagerRepository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static uk.co.asepstrath.bank.Constants.*;

public class ViewManagerDashboardService extends BaseService {

    private static ManagerRepository managerRepository;

    public ViewManagerDashboardService(DataSource datasource, Logger logger) {
        super(datasource, logger);
        managerRepository = new ManagerRepository(logger);
    }

    public ModelAndView<Map<String, Object>> renderDashboard() {
        List<Account> accounts;
        Map<String, Object> model = createModel();
        try {
            accounts = managerRepository.getAllAccounts(getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        model.put(ACCOUNT_OBJECT_LIST_EXISTS, !accounts.isEmpty());
        model.put(ACCOUNT_OBJECT_LIST, accounts);
        return render(URL_PAGE_MANAGER_DASHBOARD, model);
    }
}
