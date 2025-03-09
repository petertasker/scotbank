package uk.co.asepstrath.bank;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.asepstrath.bank.services.repository.AccountRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * The Transaction class
 */
public class Transaction {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", shape = JsonFormat.Shape.STRING)
    @JacksonXmlProperty(localName = "timestamp")
    private DateTime timestamp;
    @JacksonXmlProperty(localName = "amount")
    private BigDecimal amount;
    @JacksonXmlProperty(localName = "from")
    private String from;
    @JacksonXmlProperty(localName = "id")
    private String id;
    @JacksonXmlProperty(localName = "to")
    private String to;
    @JacksonXmlProperty(localName = "type")
    private String type;
    private boolean transactionStatus;

    private AccountRepository accountRepository;
    private Logger logger;


    public Transaction() {
        // For jackson mapper
    }

    // For transactions where the status is resolved
    public Transaction(DateTime timestamp, BigDecimal amount, String from, String id, String to, String type,
                       boolean transactionStatus) {
        this.timestamp = timestamp;
        this.amount = amount;
        this.from = from;
        this.id = id;
        this.to = to;
        this.type = type;
        this.transactionStatus = transactionStatus;

    }

    // For transactions where the status is unresolved
    public Transaction(Connection connection, DateTime timestamp, BigDecimal amount, String from, String id, String to,
                       String type) throws SQLException {
        this.timestamp = timestamp;
        this.amount = amount;
        this.from = from;
        this.id = id;
        this.to = to;
        this.type = type;
        logger = LoggerFactory.getLogger(this.getClass());
        accountRepository = new AccountRepository(logger);
        this.transactionStatus = processTransaction(connection);
    }

    /**
     * Gets the DateTime of the Transaction
     *
     * @return the "timestamp" of the Transaction
     */
    public DateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the amount value of the Transaction
     *
     * @return the "amount" of the Transaction
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Gets the sender of the Transaction
     *
     * @return the "from" of the Transaction
     */
    public String getFrom() {
        return from;
    }

    /**
     * Gets the unique ID of the Transaction
     *
     * @return the "id" of the Transaction
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the recipient of the Transaction
     *
     * @return the "to" of the Transaction
     */
    public String getTo() {
        return to;
    }

    /**
     * Gets the type of transaction (Transfer, Payment...)
     *
     * @return The "type" of Transaction
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the boolean value of if the Transaction was accepted or declined
     *
     * @return the status of the Transaction
     */
    public boolean getStatus() {
        return transactionStatus;
    }

    private boolean processTransaction(Connection connection) throws SQLException {
        if (!validateAccountsForTransactionType()) {
            return false;
        }

        try {
            return switch (getType()) {
                case "DEPOSIT" -> handleDeposit(connection);
                case "WITHDRAWAL" -> handleWithdrawal(connection);
                case "TRANSFER" -> handleTransfer(connection);
                case "PAYMENT" -> handlePayment(connection);
                default -> {
                    logger.info("Unknown transaction type: {}", this);
                    yield true;
                }
            };
        }
        catch (ArithmeticException e) {
            return false;
        }
    }

    private boolean validateAccountsForTransactionType() {
        return switch (getType()) {
            case "DEPOSIT" -> getTo() != null;
            case "WITHDRAWAL" -> getFrom() != null;
            case "TRANSFER", "PAYMENT" -> getFrom() != null && getTo() != null;
            default -> true;
        };
    }

    private boolean handleDeposit(Connection connection) throws SQLException {
        Account account = accountRepository.getAccount(connection, getTo());
        if (account == null) {
            return false;
        }

        account.deposit(getAmount());
        accountRepository.updateBalance(connection, account);
        return true;
    }

    private boolean handleWithdrawal(Connection connection) throws SQLException {
        Account account = accountRepository.getAccount(connection, getFrom());
        if (account == null) {
            logger.info("account is null");
            return false;
        }

        account.withdraw(getAmount());
        accountRepository.updateBalance(connection, account);
        return true;
    }

    private boolean handleTransfer(Connection connection) throws SQLException {
        Account sender = accountRepository.getAccount(connection, getFrom());
        Account receiver = accountRepository.getAccount(connection, getTo());

        if (sender == null || receiver == null) {
            return false;
        }

        sender.withdraw(getAmount());
        receiver.deposit(getAmount());
        accountRepository.updateBalance(connection, sender);
        accountRepository.updateBalance(connection, receiver);
        return true;
    }

    private boolean handlePayment(Connection connection) throws SQLException {
        Account payer = accountRepository.getAccount(connection, getFrom());
        if (payer == null) {
            return false;
        }

        payer.withdraw(getAmount());
        accountRepository.updateBalance(connection, payer);

        try {
            payer.withdraw(getAmount());
            accountRepository.updateBalance(connection, payer);
            return true;
        }
        catch (ArithmeticException e) {
            return false;
        }
    }

    /**
     * Returns a String interpretation of a Transaction
     *
     * @return A String of the Transaction
     */
    public String toString() {
        return "Transaction [timestamp=" + timestamp + ", amount=" + amount + ", from=" + from + ", id=" + id + ", " +
                "to=" + to + ", type=" + type + ", transactionStatus=" + transactionStatus + "]";
    }

    private void setBalance(BigDecimal amount) {
        this.amount = amount;
    }
}
