//package uk.co.asepstrath.bank.controllers;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import io.jooby.Context;
//import io.jooby.ModelAndView;
//import io.jooby.annotation.GET;
//import io.jooby.annotation.POST;
//import io.jooby.annotation.Path;
//import kong.unirest.core.UnirestException;
//import org.slf4j.Logger;
//import uk.co.asepstrath.bank.Account;
//
//import javax.sql.DataSource;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Path("/dashboard")
//public class DashboardController {
//
//    private final DataSource dataSource;
//    private final Logger logger;
//    private final ContextManager contextManager;
//
//    public DashboardController(DataSource dataSource, Logger logger) {
//        this.dataSource = dataSource;
//        this.logger = logger;
//        logger.info("Dashboard Controller initialised");
//        this.contextManager = new ContextManager();
//    }
//
//    @GET
//    public ModelAndView getDashboard(Context ctx) throws JsonProcessingException {
//            try {
//            Map<String, Object> model = new HashMap<>();
//
//            Customer customer = contextManager.getCustomerFromContext(ctx);
//            model.put("customer", customer);
//            model.put("customername", customer.getUserName());
//
//            // Get all accounts belonging to the customer as a list, or produce an empty list
//            List<Account> customerAccounts = contextManager.getAccountsFromContext(ctx)
//                .orElse(new ArrayList<>())
//                .stream()
//                .filter(account -> account.getCustomerID().equals(customer.getUserID()))
//                .toList();
//
//            model.put("accounts", customerAccounts);
//            model.put("hasaccounts", !customerAccounts.isEmpty());
//            return new ModelAndView("dashboard.hbs", model);
//        }
//        catch (Exception e) {
//            throw new UnirestException(e);
//        }
//    }
//
//    /**
//     * Log in user and send to dashboard
//     */
//    @POST
//    public ModelAndView loginUser(Context ctx) throws UnirestException {
//        try {
//            // Get from data via POST
//            String name = ctx.form("name").value();
//            String email = ctx.form("email").value();
//
//            Customer customer = new Customer(name, email);
//            contextManager.addCustomerToContext(customer, ctx);
//
//            // view new model
//            Map<String, Object> model = new HashMap<>();
//            model.put("customername", customer.getUserName());
//            return new ModelAndView("/dashboard.hbs", model);
//        }
//        catch (Exception e) {
//            throw new UnirestException(e.getMessage());
//        }
//    }
//
//
//}