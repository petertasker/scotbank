package uk.co.asepstrath.bank.controllers;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.annotation.GET;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import org.slf4j.Logger;

import java.util.Map;

import uk.co.asepstrath.bank.services.login.ServiceLoginDisplay;
import uk.co.asepstrath.bank.services.login.ServiceLoginProcess;

@Path("/login")
public class ControllerLogin extends Controller {
    private final ServiceLoginDisplay serviceLoginDisplayService;
    private final ServiceLoginProcess serviceLoginProcessService;

    public ControllerLogin(ServiceLoginDisplay serviceLoginDisplay, ServiceLoginProcess serviceLoginProcess, Logger logger) {
        super(logger);
        this.serviceLoginDisplayService = serviceLoginDisplay;
        this.serviceLoginProcessService = serviceLoginProcess;
    }

    @GET
    public ModelAndView<Map<String, Object>> displayLogin() {
        return serviceLoginDisplayService.displayLogin();
    }

    @POST
    @Path("/process")
    public ModelAndView<Map<String, Object>> processLogin(Context ctx) {
        return serviceLoginProcessService.processLogin(ctx);
    }
}