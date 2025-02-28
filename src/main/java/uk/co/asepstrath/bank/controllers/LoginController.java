package uk.co.asepstrath.bank.controllers;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.annotation.GET;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import org.slf4j.Logger;

import java.util.Map;

import uk.co.asepstrath.bank.services.login.DisplayLoginService;
import uk.co.asepstrath.bank.services.login.ProcessLoginService;

@Path("/login")
public class LoginController extends BaseController {
    private final DisplayLoginService serviceDisplayLoginService;
    private final ProcessLoginService serviceLoginProcessServiceLoginService;

    public LoginController(DisplayLoginService displayLoginService, ProcessLoginService processLoginService, Logger logger) {
        super(logger);
        this.serviceDisplayLoginService = displayLoginService;
        this.serviceLoginProcessServiceLoginService = processLoginService;
    }

    @GET
    public ModelAndView<Map<String, Object>> displayLogin() {
        return serviceDisplayLoginService.displayLogin();
    }

    @POST
    @Path("/process")
    public ModelAndView<Map<String, Object>> processLogin(Context ctx) {
        return serviceLoginProcessServiceLoginService.processLogin(ctx);
    }
}