package uk.co.asepstrath.bank.services.account;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.annotation.GET;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Constants;
import uk.co.asepstrath.bank.Transaction;
import uk.co.asepstrath.bank.services.BaseService;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

import static uk.co.asepstrath.bank.Constants.*;

/**
 * The Account Viewing service
 */
public class AccountViewService extends BaseService {

    public AccountViewService(DataSource datasource, Logger logger){
        super(datasource, logger);
    }

    /**
     * Displays the "/account" endpoint
     * @param ctx Session Context
     * @return the "/account" endpoint
     * @throws SQLException Transactions not found in database
     */
    @GET
    public ModelAndView<Map<String, Object>> viewAccount(Context ctx) throws SQLException {
        Map<String, Object> model = createModel();
        Session session = getSession(ctx);
        model.put(SESSION_ACCOUNT_NAME, session.get(SESSION_ACCOUNT_NAME));
        model.put(SESSION_ACCOUNT_ID, session.get(SESSION_ACCOUNT_ID));
        logger.info("Put name and accountid in model");

        putBalanceInModel(model, String.valueOf(session.get(SESSION_ACCOUNT_ID)));
        putRoundUpBalanceInModel(model, String.valueOf(session.get(SESSION_ACCOUNT_ID)));
        transferSessionAttributeToModel(ctx, SESSION_SUCCESS_MESSAGE, model);
        transferSessionAttributeToModel(ctx, Constants.SESSION_ERROR_MESSAGE, model);


        // Get all transactions related to a user's account
        try (Connection connection = getConnection()) {
            List<Transaction> transactions = new ArrayList<>();

            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT Timestamp, Amount, SenderID, TransactionID, ReceiverAccountID, ReceiverBusinessID, TransactionType, TransactionAccepted " +
                            "FROM Transactions " +
                            "WHERE SenderID = ? OR ReceiverAccountID = ? OR ReceiverBusinessID = ? " +
                            "ORDER BY Timestamp DESC"
            )) {
                String accountId = String.valueOf(session.get(SESSION_ACCOUNT_ID));
                preparedStatement.setString(1, accountId);
                preparedStatement.setString(2, accountId);
                preparedStatement.setString(3, accountId);

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
            model.put(TRANSACTION_OBJECT_LIST_EXISTS, !transactions.isEmpty());
        }
        return render(TEMPLATE_ACCOUNT, model);
    }

    /**
     * Gets the roundUp balance for an account and adds it to the model
     * @param model The model to add the roundUp balance to
     * @param accountId The account ID to get the roundUp balance for
     * @throws SQLException Database connection failure
     */
    private void putRoundUpBalanceInModel(Map<String, Object> model, String accountId) throws SQLException {
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT RoundUpAmount, RoundUpEnabled FROM Accounts WHERE AccountID = ?")) {
                statement.setString(1, accountId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        boolean roundUpEnabled = resultSet.getBoolean("RoundUpEnabled");
                        model.put("roundUpEnabled", roundUpEnabled);

                        if (roundUpEnabled) {
                            BigDecimal roundUpAmount = resultSet.getBigDecimal("RoundUpAmount");
                            model.put("roundUpBalance", roundUpAmount);
                        }
                    }
                }
            }
        }
    }

}
