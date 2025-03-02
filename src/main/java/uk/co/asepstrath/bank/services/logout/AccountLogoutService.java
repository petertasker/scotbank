package uk.co.asepstrath.bank.services.logout;

import io.jooby.Context;
import io.jooby.ModelAndView;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.services.BaseService;

import java.util.Map;

import static uk.co.asepstrath.bank.Constants.*;

/**
 * The account log out service
 */
public class AccountLogoutService extends BaseService {
    public AccountLogoutService(Logger logger) {
        super(logger);
    }

    /**
     * Logs the user out
     * @param ctx Session context
     * @return the "/login" endpoint with a logout success message
     */
    public ModelAndView<Map<String, Object>> logout(Context ctx) {
        ctx.session().destroy();
        Map<String, Object> model = createModel();
        model.put(URL_SUCCESS_MESSAGE, "Logged out successfully");
        return render(URL_PAGE_LOGIN, model);
    }
}
