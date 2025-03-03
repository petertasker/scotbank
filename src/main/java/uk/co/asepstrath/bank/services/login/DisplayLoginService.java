package uk.co.asepstrath.bank.services.login;

import io.jooby.Context;
import io.jooby.ModelAndView;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.services.BaseService;

import static uk.co.asepstrath.bank.Constants.*;

import java.util.Map;

/**
 * The Display login service
 */
public class DisplayLoginService extends BaseService {

    public DisplayLoginService(Logger logger) {
        super(logger);
    }

    /**
     * Renders the login endpoint
     * @return The "/login" endpoint
     */
    public ModelAndView<Map<String, Object>> displayLogin(Context ctx) {
        Map<String, Object> model = createModel();
        transferSessionAttributeToModel(ctx, SESSION_ERROR_MESSAGE, model);
        transferSessionAttributeToModel(ctx, SESSION_SUCCESS_MESSAGE, model);
        return render(TEMPLATE_LOGIN, model);
    }
}
