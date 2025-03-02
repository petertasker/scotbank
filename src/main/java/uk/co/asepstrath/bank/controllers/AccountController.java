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

/**
 * The Account endpoint controller
 */
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

    /**
     * Renders the account page
     * @param ctx Session context
     * @return The "/account" endpoint
     * @throws SQLException Failed to find an account with the session account identifier
     */
   @GET
   public ModelAndView<Map<String, Object>> viewAccount(Context ctx) throws SQLException {
        return viewAccountService.viewAccount(ctx);
    }

    /**
     * Renders the deposit page
     * @param ctx Session context
     * @return The "/deposit" endpoint
     */
    @GET
    @Path("/deposit")
    public ModelAndView<Map<String, Object>> deposit(Context ctx) {
        return depositService.renderDeposit(ctx);
    }


    /**
     * Renders the withdrawal page
     * @param ctx Session context
     * @return The "/account/withdraw" endpoint
     */
    @GET
    @Path("/withdraw")
    public ModelAndView<Map<String, Object>> withdraw(Context ctx) {
       return withdrawService.renderWithdraw(ctx);
    }

    /**
     * Engages the withdrawal process
     * @param ctx Session Context
     * @return The "/account/withdraw" endpoint on failure
     * Redirects to "/account" on success
     * @throws SQLException on withdrawal failure
     */
    @POST
    @Path("/withdraw/process")
    ModelAndView<Map<String, Object>> withdrawProcess(Context ctx) throws SQLException {
       return withdrawService.withdrawProcess(ctx);
    }

    /**
     * Engages the deposit process
     * @param ctx Session Context
     * @return The "/account/deposit" endpoint on failure
     * Redirects to "/account" on success
     * @throws SQLException on deposit failure
     */
    @POST
    @Path("/deposit/process")
    public ModelAndView<Map<String, Object>> depositProcess(Context ctx) throws SQLException {
        return depositService.processDeposit(ctx);
    }
}
