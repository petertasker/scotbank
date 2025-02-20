package uk.co.asepstrath.bank.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.annotation.GET;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import io.jooby.annotation.PathParam;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.ContextManager;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@Path("/account")
public class AccountController {

    private final DataSource dataSource;
    private final Logger logger;
    private final ContextManager contextManager = new ContextManager();

   public AccountController(DataSource dataSource, Logger logger) {
       this.dataSource = dataSource;
       this.logger = logger;
       logger.info("Account Controller initialised");
   }

   @GET
   public ModelAndView viewAccount(Context ctx) {
       Map<String, Object> model = new HashMap<>();
       Session session = ctx.session();
       model.put("name", session.get("name"));
       model.put("accountid", session.get("accountid"));
       return new ModelAndView("account.hbs", model);
   }
}
