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

import static uk.co.asepstrath.bank.Constants.ROUTE_LOGIN;
import static uk.co.asepstrath.bank.Constants.ROUTE_PROCESS;

/**
 * The Login process endpoint controller
 */
@Path(ROUTE_LOGIN)
public class LoginController extends BaseController {
    private final DisplayLoginService displayLoginService;
    private final ProcessLoginService processLoginService;

    public LoginController(DisplayLoginService displayLoginService, ProcessLoginService processLoginService, Logger logger) {
        super(logger);
        this.displayLoginService = displayLoginService;
        this.processLoginService = processLoginService;
    }

    /**
     * Displays the login endpoint
     * @return ModelAndView
     */
    @GET
    public ModelAndView<Map<String, Object>> displayLogin() {
        return displayLoginService.displayLogin();
    }

    /**
     * Directs the login process
     * @param ctx Session context
     * @return The "/login" endpoint on failure
     * Redirects to "/account" on success
     */
    @POST
    @Path(ROUTE_PROCESS)
    public ModelAndView<Map<String, Object>> processLogin(Context ctx) {
        return processLoginService.processLogin(ctx);
    }
}