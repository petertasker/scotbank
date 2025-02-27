package uk.co.asepstrath.bank.services.login;

import io.jooby.ModelAndView;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.services.Service;

import static uk.co.asepstrath.bank.Constants.*;

import java.util.Map;

public class ServiceLoginDisplay extends Service {

    public ServiceLoginDisplay(Logger logger) {
        super(logger);
    }

    public ModelAndView<Map<String, Object>> displayLogin() {
        Map<String, Object> model = createModel();
        return render(URL_PAGE_LOGIN, model);
    }
}
