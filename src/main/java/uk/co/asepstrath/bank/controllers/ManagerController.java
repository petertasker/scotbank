package uk.co.asepstrath.bank.controllers;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.annotation.GET;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.services.manager.ProcessManagerLoginService;
import uk.co.asepstrath.bank.services.manager.ViewManagerDashboardService;
import uk.co.asepstrath.bank.services.manager.ViewManagerLoginService;

import javax.sql.DataSource;
import java.util.Map;

@Path("/manager")
public class ManagerController extends BaseController {

    private final ViewManagerDashboardService viewManagerDashboardService;
    private final ViewManagerLoginService viewManagerLoginService;
    private final ProcessManagerLoginService processManagerLoginService;

    public ManagerController(DataSource dataSource, Logger logger) {
        super(logger);
        viewManagerDashboardService = new ViewManagerDashboardService(dataSource, logger);
        viewManagerLoginService = new ViewManagerLoginService(logger);
        processManagerLoginService = new ProcessManagerLoginService(dataSource, logger);
    }

    @GET
    @Path("/dashboard")
    public ModelAndView<Map<String, Object>> displayManagerDashboard() {
        return viewManagerDashboardService.renderDashboard();
    }

    @GET
    @Path("/login")
    public ModelAndView<Map<String, Object>> displayManagerLogin() {
        return viewManagerLoginService.displayManagerLogin();
    }

    @POST
    @Path("/login/process")
    public ModelAndView<Map<String, Object>> processManagerLogin(Context ctx) {
        return processManagerLoginService.processManagerLogin(ctx);
    }

}
