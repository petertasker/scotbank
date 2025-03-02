package uk.co.asepstrath.bank.services.logout;

import io.jooby.Context;
import io.jooby.ModelAndView;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.services.BaseService;

import java.util.Map;

import static uk.co.asepstrath.bank.Constants.URL_PAGE_MANAGER_LOGIN;
import static uk.co.asepstrath.bank.Constants.URL_SUCCESS_MESSAGE;

/**
 * The manager log out service
 */
public class ManagerLogoutService extends BaseService {

    public ManagerLogoutService(Logger logger) {
        super(logger);
    }

    /**
     * Logs the manager out
     * @param ctx Session context
     * @return the "/manager/login" endpoint with a logout success message
     */
    public ModelAndView<Map<String, Object>> logout(Context ctx) {
        ctx.session().destroy();
        Map<String, Object> model = createModel();
        model.put(URL_SUCCESS_MESSAGE, "Logged out successfully");
        return render(URL_PAGE_MANAGER_LOGIN, model);
    }
}
