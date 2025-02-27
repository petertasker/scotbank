package uk.co.asepstrath.bank.controllers;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.annotation.GET;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.services.account.AccountViewService;
import uk.co.asepstrath.bank.services.account.AccountDepositService;
import uk.co.asepstrath.bank.services.account.AccountWithdrawService;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;


@Path("/account")
public class AccountController extends BaseController {

    private final AccountViewService viewAccountService;
    private final AccountDepositService depositService;
    private final AccountWithdrawService withdrawService;

   public AccountController(DataSource datasource, Logger logger) {
       super(logger);
       viewAccountService = new AccountViewService(datasource, logger);
       depositService = new AccountDepositService(datasource, logger);
       withdrawService = new AccountWithdrawService(datasource, logger);
   }

   @GET
   public ModelAndView<Map<String, Object>> viewAccount(Context ctx) throws SQLException {
        return viewAccountService.viewAccount(ctx);
    }

    @GET
    @Path("/deposit")
    public ModelAndView<Map<String, Object>> deposit(Context ctx) {
        return depositService.renderDeposit(ctx);
    }


    @GET
    @Path("/withdraw")
    public ModelAndView<Map<String, Object>> withdraw(Context ctx) {
       return withdrawService.renderWithdraw(ctx);
    }

    @POST
    @Path("/withdraw/process")
    ModelAndView<Map<String, Object>> withdrawProcess(Context ctx) throws SQLException {
       return withdrawService.withdrawProcess(ctx);
    }

    @POST
    @Path("/deposit/process")
    public ModelAndView<Map<String, Object>> depositProcess(Context ctx) throws SQLException {
        return depositService.processDeposit(ctx);
    }
}
