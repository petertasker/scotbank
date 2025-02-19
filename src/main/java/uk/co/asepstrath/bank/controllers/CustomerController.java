///**
// * Customer focused endpoints:
// *  - Log in Customer
// */
//
//package uk.co.asepstrath.bank.controllers;
//
//import io.jooby.*;
//import io.jooby.annotation.*;
//import org.slf4j.Logger;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
//public class CustomerController {
//
//    private final DataSource dataSource;
//    private final Logger logger;
//
//    public CustomerController(DataSource dataSource, Logger logger) {
//        this.dataSource = dataSource;
//        this.logger = logger;
//        logger.info("UserController initialised");
//    }
//
//    /**
//     * Main Login Page
//     */
//    @GET
//    @Path("/login")
//    public ModelAndView DisplayLogin() {
//        Map<String, Object> model = new HashMap<>();
//        return new ModelAndView("/loginform.hbs", model);
//    }
//
//
//
////    /**
////     * Main Register Page
////     */
////    @GET
////    @Path("/register")
////    public ModelAndView DisplayRegister() {
////        Map<String, Object> model = new HashMap<>();
////        return new ModelAndView("registerform.hbs", model);
////    }
////
////    /**
////     * Register a new user and add to session as JSON object
////     */
////    @POST
////    @Path("/register")
////    @Consumes("application/json") // Take in a JSON object
////    @Produces("application/json") // Produce a JSON object
////    public User registerUser(Context ctx) {
////        try {
////            ObjectMapper mapper = new ObjectMapper();
////            Map<String, String> formData = new HashMap<>();
////            formData.put("fullname", ctx.form("fullname").value());
////            formData.put("email", ctx.form("email").value());
////            String jsonString = mapper.writeValueAsString(formData); // Map register data into JSON object cast to string
////            User user = mapper.readValue(jsonString, User.class); // Create java user and put into the session
////            ctx.session().put("user", jsonString);
////
////            return user;
////        }
////        catch (JsonProcessingException e) {
////            throw new RuntimeException(e);
////        }
////    }
////
////    @GET("/listaccounts")
////    public List<Account> getAccounts() {
////        return AccountManager.generateExampleAccounts();
////    }
//
//
//
//}