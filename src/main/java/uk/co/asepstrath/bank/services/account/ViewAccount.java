package uk.co.asepstrath.bank.services.account;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.annotation.GET;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Transaction;
import uk.co.asepstrath.bank.services.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

import static uk.co.asepstrath.bank.Constants.*;

public class ViewAccount extends Service {

    public ViewAccount(DataSource datasource, Logger logger){
        super(datasource, logger);
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
                        transactions.add(transaction);
                    }
                }
            }

            model.put(TRANSACTION_OBJECT_LIST, transactions);
            model.put(TRANSACTION_OBJECT_EXISTS, !transactions.isEmpty());
        }
        return new ModelAndView<>(URL_PAGE_ACCOUNT, model);
    }
}
