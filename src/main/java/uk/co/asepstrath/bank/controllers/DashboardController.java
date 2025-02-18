package uk.co.asepstrath.bank.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.annotation.GET;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import kong.unirest.core.UnirestException;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.ContextManager;
import uk.co.asepstrath.bank.Customer;
import uk.co.asepstrath.bank.User;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Path("/dashboard")
public class DashboardController {

    private final DataSource dataSource;
    private final Logger logger;

    public DashboardController(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;
        logger.info("Dashboard Controller initialised");
    }

    @GET
    public ModelAndView getDashboard(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        Session session = ctx.session();
        String userJson = String.valueOf(session.get("user"));
        model.put("username", userJson);
        return new ModelAndView("dashboard.hbs", model);
    }

    /**
     * Log in user and send to dashboard
     */
    @POST
    public ModelAndView loginUser(Context ctx) throws UnirestException {
        try {
            // Get from data via POST
            String name = ctx.form("name").value();
            String email = ctx.form("email").value();

            Customer customer = new Customer(name, email);
            ContextManager contextManager = new ContextManager();
            contextManager.putCustomerIntoContext(customer, ctx);

            // view new model
            Map<String, Object> model = new HashMap<>();
            model.put("username", customer.getUserName());
            return new ModelAndView("/dashboard.hbs", model);
        }
        catch (Exception e) {
            throw new UnirestException(e.getMessage());
        }
    }


}