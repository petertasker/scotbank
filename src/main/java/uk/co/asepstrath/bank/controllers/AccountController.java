package uk.co.asepstrath.bank.controllers;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.annotation.GET;
import io.jooby.annotation.Path;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.ContextManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


@Path("/account")
public class AccountController {

    private final Logger logger;
    private final ContextManager contextManager = new ContextManager();

   public AccountController(DataSource dataSource, Logger logger) {
       this.logger = logger;
       logger.info("Account Controller initialised");
   }

   @GET
   public ModelAndView<Map<String, Object>> viewAccount(Context ctx) {
       Map<String, Object> model = new HashMap<>();
       Session session = ctx.session();
       model.put("name", session.get("name"));
       model.put("accountid", session.get("accountid"));
       logger.info("Put name and accountid in model");
       return new ModelAndView<>("account.hbs", model);
   }
}
