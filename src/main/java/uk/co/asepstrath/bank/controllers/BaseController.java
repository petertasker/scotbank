package uk.co.asepstrath.bank.controllers;

import io.jooby.ModelAndView;
import org.slf4j.Logger;

import java.util.Map;

public abstract class BaseController {

    protected final Logger logger;

    protected BaseController(Logger logger) {
        this.logger = logger;
        this.logger.info("{} created", this.getClass().getName());
    }
}
