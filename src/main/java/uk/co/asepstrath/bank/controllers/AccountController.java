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
       model.put(URL_ACCOUNT_NAME, session.get("name"));
       model.put(URL_ACCOUNT_ID, session.get("accountid"));
       logger.info("Put name and accountid in model");

       // Get all transactions related to a users account
       try (Connection connection = dataSource.getConnection()) {
           List<Transaction> transactions = new ArrayList<>();
           PreparedStatement preparedStatement = connection.prepareStatement(
                   "SELECT Timestamp, Amount, SenderID, TransactionID, ReceiverID, TransactionType " +
                           "FROM Transactions " +
                           "WHERE SenderID = ?"
           );
           preparedStatement.setString(1, String.valueOf(session.get(URL_ACCOUNT_ID)));
           ResultSet resultSet = preparedStatement.executeQuery();
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
               model.put(URL_TRANSACTION_OBJECTS, transactions);
               model.put(URL_TRANSACTION_HAS_OBJECTS, !transactions.isEmpty());
           }
       }
       return new ModelAndView<>(URL_PAGE_ACCOUNT, model);
   }
}
