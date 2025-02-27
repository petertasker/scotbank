package uk.co.asepstrath.bank.controllers;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.annotation.GET;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.services.account.ViewAccount;
import uk.co.asepstrath.bank.services.account.Deposit;
import uk.co.asepstrath.bank.services.account.Withdraw;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;


@Path("/account")
public class AccountController extends Controller {

    private final ViewAccount viewAccountService;
    private final Deposit depositService;
    private final Withdraw withdrawService;

   public AccountController(DataSource datasource, Logger logger) {
       super(logger);
       viewAccountService = new ViewAccount(datasource, logger);
       depositService = new Deposit(datasource, logger);
       withdrawService = new Withdraw(datasource, logger);
   }

   @GET
   public ModelAndView<Map<String, Object>> viewAccount(Context ctx) throws SQLException {
        return viewAccountService.viewAccount(ctx);
    }

    @GET
    @Path("/deposit")
    public ModelAndView<Map<String, Object>> deposit(Context ctx) {
        return depositService.displayDeposit(ctx);
    }


    @GET
    @Path("/withdraw")
    public ModelAndView<Map<String, Object>> withdraw(Context ctx) {
       return withdrawService.withdraw(ctx);
    }

    @POST
    @Path("/withdraw/process")
    ModelAndView<Map<String, Object>> withdrawProcess(Context ctx) throws SQLException {
       return withdrawService.withdrawProcess(ctx);
    }

    @POST
    @Path("/deposit/process")
    public ModelAndView<Map<String, Object>> depositProcess(Context ctx) throws SQLException {
        return depositService.depositProcess(ctx);
    }
}
