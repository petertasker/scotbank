package uk.co.asepstrath.bank.controllers;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.annotation.GET;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.DatabaseHandler;
import uk.co.asepstrath.bank.Transaction;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

import static uk.co.asepstrath.bank.Constants.*;


@Path("/account")
public class AccountController {

    private final Logger logger;
    private final DataSource dataSource;
    private final DatabaseHandler databaseHandler;

   public AccountController(DataSource datasource, Logger logger) {
       this.dataSource = datasource;
       this.logger = logger;
       databaseHandler = new DatabaseHandler();
       logger.info("Account Controller initialised");
   }

    @GET
    public ModelAndView<Map<String, Object>> viewAccount(Context ctx) throws SQLException {
        Map<String, Object> model = new HashMap<>();
        Session session = ctx.session();
        model.put(SESSION_ACCOUNT_NAME, session.get("name"));
        model.put(SESSION_ACCOUNT_ID, session.get("accountid"));
        logger.info("Put name and accountid in model");

        putBalanceInModel(model, String.valueOf(session.get(SESSION_ACCOUNT_ID)));


        // Get all transactions related to a user's account
        try (Connection connection = dataSource.getConnection()) {
            List<Transaction> transactions = new ArrayList<>();

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT Timestamp, Amount, SenderID, TransactionID, ReceiverAccountID, ReceiverBusinessID, TransactionType, TransactionAccepted " +
                            "FROM Transactions " +
                            "WHERE SenderID = ?"
            )) {
                preparedStatement.setString(1, String.valueOf(session.get(SESSION_ACCOUNT_ID)));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Timestamp timestamp = resultSet.getTimestamp("Timestamp");
                        DateTime dateTime = new DateTime(timestamp);
                        BigDecimal amount = resultSet.getBigDecimal("Amount");
                        String senderID = resultSet.getString("SenderID");
                        String transactionID = resultSet.getString("TransactionID");
                        String receiverAccountID = resultSet.getString("ReceiverAccountID");
                        String receiverBusinessID = resultSet.getString("ReceiverBusinessID");
                        String receiverID;
                        if (receiverAccountID != null) {
                            receiverID = receiverAccountID;
                        }
                        else {
                            receiverID = receiverBusinessID;
                        }
                        String transactionType = resultSet.getString("TransactionType");
                        boolean transactionAccepted = resultSet.getBoolean("TransactionAccepted");
                        Transaction transaction = new Transaction(dateTime, amount, senderID, transactionID, receiverID, transactionType, transactionAccepted);
                        // logger.info("Found a transaction for this account: {}", transaction.toString());
                        transactions.add(transaction);
                    }
                }
            }

            model.put(TRANSACTION_OBJECT_LIST, transactions);
            model.put(TRANSACTION_OBJECT_EXISTS, !transactions.isEmpty());
        }
        return new ModelAndView<>(URL_PAGE_ACCOUNT, model);
    }

    @GET
    @Path("/deposit")
    public ModelAndView<Map<String, Object>> deposit(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        Session session = ctx. session();

        putBalanceInModel(model, String.valueOf(session.get(SESSION_ACCOUNT_ID)));
        return new ModelAndView<>(URL_PAGE_ACCOUNT_DEPOSIT, model);
    }


    @GET
    @Path("/withdraw")
    public ModelAndView<Map<String, Object>> withdraw(Context ctx) {
       Map<String, Object> model = new HashMap<>();
       Session session = ctx. session();

       putBalanceInModel(model, String.valueOf(session.get(SESSION_ACCOUNT_ID)));
       return new ModelAndView<>(URL_PAGE_ACCOUNT_WITHDRAW, model);
    }

    private void putBalanceInModel(Map<String, Object> model, String accountId) {
        BigDecimal balance = BigDecimal.ZERO;
        try(PreparedStatement statement = dataSource.getConnection().prepareStatement("select Balance from Accounts where AccountID = ?")) {
            statement.setString(1, accountId);
            ResultSet rs = statement.executeQuery();
            try(rs){
                if (rs.next()) {
                    balance = rs.getBigDecimal("Balance");
                    logger.info("Account balance: {}" , balance);
                }else{
                    logger.info("Account balance is empty");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        model.put("balance", balance);
    }

    @POST
    @Path("/withdraw/process")
    ModelAndView<Map<String, Object>> withdrawProcess(Context ctx) throws SQLException {
       try (Connection connection = dataSource.getConnection()) {
           Session session = ctx.session();
           String accountId = String.valueOf(session.get(SESSION_ACCOUNT_ID));
           BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(ctx.form("withdrawlamount").value()));
           Account account = databaseHandler.fetchAccount(connection, accountId);
           Map<String, Object> model = new HashMap<>();
           try {
               account.withdraw(amount);
               updateDatabaseBalance(account);
               ctx.sendRedirect("/account");
           } catch (ArithmeticException e) {
               logger.error(e.getMessage());
               model.put(URL_ERROR_MESSAGE, e.getMessage());
               putBalanceInModel(model,accountId);
               return new ModelAndView<>(URL_PAGE_ACCOUNT_WITHDRAW,model);
           }
       }
       return null;
    }

    @POST
    @Path("/deposit/process")
    public ModelAndView<Map<String, Object>> depositProcess(Context ctx) throws SQLException {
       try (Connection connection = dataSource.getConnection()) {
           Session session = ctx.session();
           String accountId = String.valueOf(session.get(SESSION_ACCOUNT_ID));
           BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(ctx.form("depositamount").value()));
           Account account = databaseHandler.fetchAccount(connection, accountId);
           Map<String, Object> model = new HashMap<>();
           try {
               account.deposit(amount);
               updateDatabaseBalance(account);
               ctx.sendRedirect("/account");
           } catch (ArithmeticException e) {
               logger.error(e.getMessage());
               model.put(URL_ERROR_MESSAGE, e.getMessage());
               putBalanceInModel(model,accountId);
               return new ModelAndView<>(URL_PAGE_ACCOUNT_DEPOSIT,model);
           }
       }
       return null;
    }

    private void updateDatabaseBalance(Account account) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE Accounts SET Balance = ? WHERE AccountID = ?")) {

            statement.setBigDecimal(1, account.getBalance());
            statement.setString(2, account.getAccountID());
            statement.executeUpdate();
        }
    }
}
