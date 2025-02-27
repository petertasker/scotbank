package uk.co.asepstrath.bank.controllers;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.annotation.GET;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import org.slf4j.Logger;

import java.util.Map;

import uk.co.asepstrath.bank.services.login.DisplayLogin;
import uk.co.asepstrath.bank.services.login.ProcessLogin;

@Path("/login")
public class LoginController {
    private final DisplayLogin displayLoginService;
    private final ProcessLogin processLoginService;
    private final Logger logger;

    public LoginController(DisplayLogin displayLogin, ProcessLogin processLogin, Logger logger) {
        this.displayLoginService =  displayLogin;
        this.processLoginService = processLogin;
        this.logger = logger;
        logger.info("Login Controller initialised");
    }

    @GET
    public ModelAndView<Map<String, Object>> displayLogin() {
        return displayLoginService.displayLogin();
    }

    @POST
    @Path("/process")
    public ModelAndView<Map<String, Object>> processLogin(Context ctx) {
        return processLoginService.processLogin(ctx);
    }
}