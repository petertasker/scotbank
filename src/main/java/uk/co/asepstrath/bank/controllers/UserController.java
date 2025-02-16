package uk.co.asepstrath.bank.controllers;

import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotation.*;
import io.jooby.exception.StatusCodeException;
import kong.unirest.core.Unirest;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.AccountManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Path("/users")
public class UserController {

    private final DataSource dataSource;
    private final Logger logger;

    public UserController(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;
        logger.info("UserController initialised");
    }

    @GET("/listaccounts")
    public List<Account> getAccounts() {
        return AccountManager.generateExampleAccounts();
    }



}