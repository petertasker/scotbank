package uk.co.asepstrath.bank.controllers;

import io.jooby.ModelAndView;
import io.jooby.annotation.GET;
import io.jooby.annotation.Path;
import org.slf4j.Logger;

import javax.sql.DataSource;
import javax.swing.text.View;
import java.util.HashMap;
import java.util.Map;


@Path("/account")
public class AccountController {

    private final DataSource dataSource;
    private final Logger logger;

    public AccountController(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;
        logger.info("Account Controller initialised");
    }

    @GET
    @Path("/openaccount")
    public ModelAndView openAccount() {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView("openaccount.hbs", model);
    }

}
