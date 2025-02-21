package uk.co.asepstrath.bank.controllers;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.annotation.GET;
import io.jooby.annotation.Path;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static uk.co.asepstrath.bank.controllers.Constants.*;


@Path("/account")
public class AccountController {

    private final Logger logger;

   public AccountController(Logger logger) {
       this.logger = logger;
       logger.info("Account Controller initialised");
   }

   @GET
   public ModelAndView<Map<String, Object>> viewAccount(Context ctx) {
       Map<String, Object> model = new HashMap<>();
       Session session = ctx.session();
       model.put(URL_ACCOUNT_NAME, session.get("name"));
       model.put(URL_ACCOUNT_ID, session.get("accountid"));
       logger.info("Put name and accountid in model");
       return new ModelAndView<>(Constants.URL_PAGE_ACCOUNT, model);
   }
}
