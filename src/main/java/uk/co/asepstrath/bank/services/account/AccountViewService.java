package uk.co.asepstrath.bank.services.account;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.annotation.GET;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Constants;
import uk.co.asepstrath.bank.Transaction;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.co.asepstrath.bank.Constants.*;

/**
 * The Account Viewing service
 */
public class AccountViewService extends AccountService {

    public AccountViewService(DataSource datasource, Logger logger) {
        super(datasource, logger);
    }

    /**
     * Displays the "/account" endpoint
     *
     * @param ctx Session Context
     * @return the "/account" endpoint
     * @throws SQLException Transactions not found in database
     */
    @GET
    public ModelAndView<Map<String, Object>> viewAccount(Context ctx) throws SQLException {
        Map<String, Object> model = createModel();
        Session session = getSession(ctx);

        // Setup basic account info
        setupBasicAccountInfo(model, session);

        // Add account balances
        String accountId = String.valueOf(session.get(SESSION_ACCOUNT_ID));
        putAccountBalancesInModel(model, accountId);

        // Transfer session messages
        transferSessionMessages(ctx, model);

        // Get transaction history
        loadTransactionHistory(model, accountId);

        return render(TEMPLATE_ACCOUNT, model);
    }

    /**
     * Sets up basic account information in the model
     */
    private void setupBasicAccountInfo(Map<String, Object> model, Session session) {
        model.put(SESSION_ACCOUNT_NAME, session.get(SESSION_ACCOUNT_NAME));
        model.put(SESSION_ACCOUNT_ID, session.get(SESSION_ACCOUNT_ID));
        logger.info("Put name and accountid in model");
    }

    /**
     * Transfers session messages to the model
     */
    private void transferSessionMessages(Context ctx, Map<String, Object> model) {
        transferSessionAttributeToModel(ctx, SESSION_SUCCESS_MESSAGE, model);
        transferSessionAttributeToModel(ctx, Constants.SESSION_ERROR_MESSAGE, model);
    }

    /**
     * Loads transaction history for an account
     */
    private void loadTransactionHistory(Map<String, Object> model, String accountId) throws SQLException {
        try (Connection connection = getConnection()) {
            // Get original transactions
            List<Transaction> transactions = fetchTransactions(connection, accountId);
            model.put(TRANSACTION_OBJECT_LIST_EXISTS, !transactions.isEmpty());

            // Create new list with display-ready transactions
            List<Map<String, Object>> displayTransactions = new ArrayList<>();

            for (Transaction transaction : transactions) {
                Map<String, Object> displayTx = new HashMap<>();
                displayTx.put("id", transaction.getId());
                displayTx.put("timestamp", transaction.getTimestamp());
                displayTx.put("from", transaction.getFrom());
                displayTx.put("to", transaction.getTo());
                displayTx.put("type", transaction.getType());
                displayTx.put("status", transaction.getStatus());

                // Format the amount
                displayTx.put("amount", formatCurrency(transaction.getAmount()));

                displayTransactions.add(displayTx);
            }

            addPaymentCountPerBusinessToModel(connection, accountId, model);
            addPaymentSumPerBusinessToModel(connection, accountId, model);
            model.put(TRANSACTION_OBJECT_LIST, displayTransactions);
        }
    }

    /**
     * Fetches transactions from the database
     */
    private List<Transaction> fetchTransactions(Connection connection, String accountId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT Timestamp, Amount, SenderID, TransactionID, ReceiverAccountID, " +
                "ReceiverBusinessID, TransactionType, TransactionAccepted " + "FROM Transactions " + "WHERE SenderID " +
                "= ? OR ReceiverAccountID = ? OR ReceiverBusinessID = ? " + "ORDER BY Timestamp DESC";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, accountId);
            preparedStatement.setString(2, accountId);
            preparedStatement.setString(3, accountId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    transactions.add(createTransactionFromResultSet(resultSet));
                }
            }
        }
        return transactions;
    }

    /**
     * Creates a Transaction object from a database result set
     */
    private Transaction createTransactionFromResultSet(ResultSet resultSet) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp("Timestamp");
        DateTime dateTime = new DateTime(timestamp);
        BigDecimal amount = resultSet.getBigDecimal("Amount");
        String senderID = resultSet.getString("SenderID");
        String transactionID = resultSet.getString("TransactionID");
        String receiverAccountID = resultSet.getString("ReceiverAccountID");
        String receiverBusinessID = resultSet.getString("ReceiverBusinessID");

        String receiverID = (receiverAccountID != null) ? receiverAccountID : receiverBusinessID;

        String transactionType = resultSet.getString("TransactionType");
        boolean transactionAccepted = resultSet.getBoolean("TransactionAccepted");

        return new Transaction(dateTime, amount, senderID, transactionID, receiverID, transactionType,
                transactionAccepted);
    }

    /**
     * Counts the number of businesses per category for all payments made by a specific account
     *
     * @param connection Database connection
     * @param accountId  The ID of the logged-in account
     */
    private void addPaymentCountPerBusinessToModel(Connection connection, String accountId,
                                                  Map<String, Object> model) throws SQLException {
        Map<String, Integer> insightMap = new HashMap<>();

        String query = """
                SELECT
                    b.Category,
                    COUNT(DISTINCT b.BusinessID) AS BusinessCount,
                FROM Transactions t
                INNER JOIN Businesses b ON t.ReceiverBusinessID = b.BusinessID
                WHERE t.SenderID = ?
                AND t.TransactionAccepted = TRUE
                AND t.ReceiverBusinessID IS NOT NULL
                AND t.TransactionType = 'PAYMENT'
                GROUP BY b.Category
                ORDER BY BusinessCount DESC""";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, accountId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String category = resultSet.getString("Category");
                    int count = resultSet.getInt("BusinessCount");
                    insightMap.put(category, count);
                }
            }
        }
        logger.info("Count businesses per category for account {} is {}", accountId, insightMap);
        model.put(BUSINESS_COUNTS, insightMap.entrySet().stream()
                .map(entry -> Map.of("category", entry.getKey(), "count", entry.getValue()))
                .collect(Collectors.toList()));

    }

    private void addPaymentSumPerBusinessToModel(Connection connection, String accountID, Map<String, Object> model) throws
            SQLException {
        Map<String, BigDecimal> insightMap = new HashMap<>();
        String query = """
                SELECT
                    b.Category,
                    SUM(t.Amount) AS TotalAmount
                FROM Transactions t
                INNER JOIN Businesses b ON t.ReceiverBusinessID = b.BusinessID
                WHERE t.SenderID = ?
                AND t.TransactionAccepted = TRUE
                AND t.ReceiverBusinessID IS NOT NULL
                AND t.TransactionType = 'PAYMENT'
                GROUP BY b.Category
                ORDER BY TotalAmount DESC""";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, accountID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String category = resultSet.getString("Category");
                    BigDecimal totalAmount = resultSet.getBigDecimal("TotalAmount");
                    insightMap.put(category, totalAmount);
                }
            }
        }
        logger.info("Sum businesses per category for account {} is {}", accountID, insightMap);
        model.put(BUSINESS_AMOUNT_SUMS, insightMap.entrySet().stream()
                .map(entry -> Map.of("category", entry.getKey(), "totalAmount", entry.getValue()))
                .collect(Collectors.toList()));
    }


}
