package uk.co.asepstrath.bank.services.logout;

import io.jooby.Context;
import io.jooby.ModelAndView;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.services.BaseService;

import java.util.Map;

import static uk.co.asepstrath.bank.Constants.URL_PAGE_MANAGER_LOGIN;
import static uk.co.asepstrath.bank.Constants.URL_SUCCESS_MESSAGE;

public class ManagerLogoutService extends BaseService {

    public ManagerLogoutService(Logger logger) {
        super(logger);
    }

    public ModelAndView<Map<String, Object>> logout(Context ctx) {
        ctx.session().destroy();
        Map<String, Object> model = createModel();
        model.put(URL_SUCCESS_MESSAGE, "Logged out successfully");
        return render(URL_PAGE_MANAGER_LOGIN, model);
    }
}
