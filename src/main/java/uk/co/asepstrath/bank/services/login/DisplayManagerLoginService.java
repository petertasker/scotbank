package uk.co.asepstrath.bank.services.login;

import io.jooby.ModelAndView;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.services.BaseService;

import java.util.Map;

import static uk.co.asepstrath.bank.Constants.URL_PAGE_MANAGER_LOGIN;

public class DisplayManagerLoginService extends BaseService {

    public DisplayManagerLoginService(Logger logger) {
        super(logger);
    }

    public ModelAndView<Map<String, Object>> displayManagerLogin() {
        Map<String, Object> model = createModel();
        return render(URL_PAGE_MANAGER_LOGIN, model);
    }
}
