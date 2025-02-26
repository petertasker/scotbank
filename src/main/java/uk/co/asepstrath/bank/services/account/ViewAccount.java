package uk.co.asepstrath.bank.services.account;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.annotation.GET;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Transaction;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

import static uk.co.asepstrath.bank.Constants.*;

public class ViewAccount {
    private final Logger logger;
    private final DataSource dataSource;
    private final ReuseServices reuseServices;

    public ViewAccount(DataSource datasource, Logger logger){
        this.dataSource = datasource;
        this.logger = logger;
        this.reuseServices = new ReuseServices(datasource, logger);
        logger.info("ViewAccount initialised");
    }

    @GET
    public ModelAndView<Map<String, Object>> viewAccount(Context ctx) throws SQLException {
        Map<String, Object> model = new HashMap<>();
        Session session = ctx.session();
        model.put(SESSION_ACCOUNT_NAME, session.get("name"));
        model.put(SESSION_ACCOUNT_ID, session.get("accountid"));
        logger.info("Put name and accountid in model");

        reuseServices.putBalanceInModel(model, String.valueOf(session.get(SESSION_ACCOUNT_ID)));

        // Get all transactions related to a user's account
        try (Connection connection = dataSource.getConnection()) {
            List<Transaction> transactions = new ArrayList<>();

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
                                resultSet.getBigDecimal("Amount"),
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
}
