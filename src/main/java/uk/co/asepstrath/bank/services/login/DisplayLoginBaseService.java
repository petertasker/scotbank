package uk.co.asepstrath.bank.services.login;

import io.jooby.ModelAndView;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.services.BaseService;

import static uk.co.asepstrath.bank.Constants.*;

import java.util.Map;

public class DisplayLoginBaseService extends BaseService {

    public DisplayLoginBaseService(Logger logger) {
        super(logger);
    }

    public ModelAndView<Map<String, Object>> displayLogin() {
        Map<String, Object> model = createModel();
        return render(URL_PAGE_LOGIN, model);
    }
}
