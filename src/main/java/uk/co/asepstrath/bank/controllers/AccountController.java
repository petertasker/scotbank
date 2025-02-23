package uk.co.asepstrath.bank.controllers;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.annotation.GET;
import io.jooby.annotation.Path;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Transaction;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.co.asepstrath.bank.Constants.*;


@Path("/account")
public class AccountController {

    private final Logger logger;
    private final DataSource dataSource;

   public AccountController(DataSource datasource, Logger logger) {
       this.dataSource = datasource;
       this.logger = logger;
       logger.info("Account Controller initialised");
   }

    @GET
    public ModelAndView<Map<String, Object>> viewAccount(Context ctx) throws SQLException {
        Map<String, Object> model = new HashMap<>();
        Session session = ctx.session();
        model.put(SESSION_ACCOUNT_NAME, session.get("name"));
        model.put(SESSION_ACCOUNT_ID, session.get("accountid"));
        logger.info("Put name and accountid in model");

        getBalance(model, String.valueOf(session.get("name")));

        // Get all transactions related to a user's account
        try (Connection connection = dataSource.getConnection()) {
            List<Transaction> transactions = new ArrayList<>();

            // Using try-with-resources to ensure ResultSet is closed properly
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT Timestamp, Amount, SenderID, TransactionID, ReceiverID, TransactionType " +
                            "FROM Transactions " +
                            "WHERE SenderID = ?"
            )) {
                preparedStatement.setString(1, String.valueOf(session.get(SESSION_ACCOUNT_ID)));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Timestamp timestamp = resultSet.getTimestamp("Timestamp");
                        DateTime dateTime = new DateTime(timestamp);

                        Transaction transaction = new Transaction(
                                dateTime,
                                resultSet.getInt("Amount"),
                                resultSet.getString("SenderID"),
                                resultSet.getString("TransactionID"),
                                resultSet.getString("ReceiverID"),
                                resultSet.getString("TransactionType")
                        );
                        logger.info("Found a transaction for this account: {}", transaction);
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
       Session session = ctx.session();
       String accName = String.valueOf(session.get("name"));
       model.put(SESSION_ACCOUNT_NAME, accName);

       getBalance(model,accName);
       return new ModelAndView<>(URL_PAGE_ACCOUNT_DEPOSIT, model);
    }


    @GET
    @Path("/withdraw")
    public ModelAndView<Map<String, Object>> withdraw(Context ctx) {
       Map<String, Object> model = new HashMap<>();
       Session session = ctx.session();
       String accName = String.valueOf(session.get("name"));

       getBalance(model,accName);
       return new ModelAndView<>(URL_PAGE_ACCOUNT_WITHDRAW, model);
    }

    private void getBalance(Map<String, Object> model, String name) {
        BigDecimal balance = BigDecimal.ZERO;

        if (name == null) {
            logger.info("Account balance is empty");
            model.put("balance", "N/a");
            return;
        }
        try(PreparedStatement statement = dataSource.getConnection().prepareStatement("select Balance from Accounts where Name = ?")) {
            statement.setString(1, name);
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
}
