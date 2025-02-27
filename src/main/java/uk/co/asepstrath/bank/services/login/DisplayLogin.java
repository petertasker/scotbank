package uk.co.asepstrath.bank.services.login;

import io.jooby.ModelAndView;
import org.slf4j.Logger;

import static uk.co.asepstrath.bank.Constants.*;

import java.util.HashMap;
import java.util.Map;

public class DisplayLogin {

    public DisplayLogin(Logger logger) {
        logger.info("Display Login Handler initialised");
    }

    public ModelAndView<Map<String, Object>> displayLogin() {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView<>(URL_PAGE_LOGIN, model);
    }
}
