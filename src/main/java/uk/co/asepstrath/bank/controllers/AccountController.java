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
    private final ContextManager contextManager;

    public AccountController(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;
        this.contextManager = new ContextManager();
        logger.info("Account Controller initialised");
    }

    @GET
    @Path("/{accountid}")
    public ModelAndView getAccount(@PathParam("accountid") String accountid) {
        Map<String, Object> model = new HashMap<>();
        model.put("account", accountid);
        return new ModelAndView("accountview.hbs", model);
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
    @Path("/create")
    public void openAccount(Context ctx) throws JsonProcessingException {
        try {
            String formBalance = ctx.form("startingbalance").value();
            BigDecimal balance = new BigDecimal(formBalance);
            Customer customer = contextManager.getCustomerFromContext(ctx);
            Account account = customer.createAccount(balance);
            contextManager.addAccountToContext(account, ctx);
            ctx.sendRedirect("/dashboard");

        } catch (Exception e) {
            logger.error("Failed to put account into context", e);
            throw e;
        }

    }
}
