package uk.co.asepstrath.bank.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.annotation.GET;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import io.jooby.annotation.PathParam;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.ContextManager;
import uk.co.asepstrath.bank.Customer;

import javax.sql.DataSource;
import javax.swing.text.View;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@Path("/account")
public class AccountController {

    private final DataSource dataSource;
    private final Logger logger;

    public AccountController(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;
        logger.info("Account Controller initialised");
    }

    @GET
    @Path("/{accountid}")
    public ModelAndView getAccount(@PathParam("accountid") String accountid) {
        Map<String, Object> model = new HashMap<>();
        model.put("account", accountid);
        return new ModelAndView("account", model);
    }

    /**
     * Open a new account
     */
    @GET
    @Path("/openaccount")
    public ModelAndView displayOpenAccount() {
        Map<String, Object> model = new HashMap<>();
        return new ModelAndView("openaccount.hbs", model);
    }

    /**
     * Open a new account
     */
    @POST
    @Path("/openaccount/process")
    public ModelAndView openAccount(Context ctx) throws JsonProcessingException {
        try {
            String formBalance = ctx.form("startingbalance").value();
            BigDecimal balance = new BigDecimal(formBalance);

            ContextManager contextManager = new ContextManager();
            Customer customer = contextManager.getCustomerFromContext(ctx);

            Account account = new Account(customer, balance);
            contextManager.putAccountIntoContext(account, ctx);

            Map<String, Object> model = new HashMap<>();
            return new ModelAndView("accountcreated.hbs", model);

        } catch (JsonProcessingException e) {
            System.out.println("Failed to put account into context");
            throw e;
        }

    }
}
