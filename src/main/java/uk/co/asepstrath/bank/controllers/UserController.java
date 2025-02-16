package uk.co.asepstrath.bank.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotation.*;
import io.jooby.exception.StatusCodeException;
import kong.unirest.core.Unirest;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.AccountManager;
import uk.co.asepstrath.bank.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UserController {

    private final DataSource dataSource;
    private final Logger logger;

    public UserController(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;
        logger.info("UserController initialised");
    }

    /**
     * Main Login Page
     */
    @GET
    @Path("/login")
    public ModelAndView DisplayLogin() {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView("loginform.hbs", model);
    }

    /**
     * Main Register Page
     */
    @GET
    @Path("/register")
    public ModelAndView DisplayRegister() {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView("registerform.hbs", model);
    }

    /**
     * Register a new user and add to session as JSON object
     */
    @POST
    @Path("/register")
//    @Consumes("application/json") // Take in a JSON object
//    @Produces("application/json") // Produce a JSON object
    public User registerUser(Context ctx) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> formData = new HashMap<>();
            formData.put("fullname", ctx.form("fullname").value());
            formData.put("email", ctx.form("email").value());
            String jsonString = mapper.writeValueAsString(formData); // Map register data into JSON object cast to string
            User user = mapper.readValue(jsonString, User.class); // Create java user and put into the session
            ctx.session().put("user", jsonString);

            return user;
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
//
//    @GET("/listaccounts")
//    public List<Account> getAccounts() {
//        return AccountManager.generateExampleAccounts();
//    }



}