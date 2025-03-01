package uk.co.asepstrath.bank.services.manager;

import io.jooby.ModelAndView;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.services.BaseService;

import java.util.Map;

import static uk.co.asepstrath.bank.Constants.URL_PAGE_MANAGER_LOGIN;

public class ViewManagerLoginService extends BaseService {

    public ViewManagerLoginService(Logger logger) {
        super(logger);
    }

    public ModelAndView<Map<String, Object>> displayManagerLogin() {
        Map<String, Object> model = createModel();
        return render(URL_PAGE_MANAGER_LOGIN, model);
    }
}
