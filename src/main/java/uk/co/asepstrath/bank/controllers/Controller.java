package uk.co.asepstrath.bank.controllers;

import io.jooby.ModelAndView;
import org.slf4j.Logger;

import java.util.Map;

public abstract class Controller {

    protected final Logger logger;

    protected Controller(Logger logger) {
        this.logger = logger;
        this.logger.info("{} created", this.getClass().getName());
    }

    protected ModelAndView<Map<String, Object>> render(String viewName, Map<String, Object> model) {
        return new ModelAndView<>(viewName, model);
    }
}
