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
import javax.sql.DataSource;

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
    public Transaction(DateTime timestamp, BigDecimal amount, String from, String id, String to, String type, boolean transactionStatus) {
        this.timestamp= timestamp;
        this.amount = amount;
        this.from = from;
        this.id = id;
        this.to = to;
        this.type = type;
        this.transactionStatus = transactionStatus;

    }

    // For transactions where the status is unresolved
    public Transaction(Connection connection, DateTime timestamp, BigDecimal amount, String from, String id, String to, String type) throws SQLException {
        this.timestamp= timestamp;
        this.amount = amount;
        this.from = from;
        this.id = id;
        this.to = to;
        this.type = type;
        logger = LoggerFactory.getLogger(this.getClass());
        accountRepository = new AccountRepository(logger);
        this.transactionStatus = processTransaction(connection);
        // logger.info("Transaction status: {}, New Balance: {}", transactionStatus, amount);
    }

    /**
     * Gets the DateTime of the Transaction
     * @return the "timestamp" of the Transaction
     */
    public DateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the amount value of the Transaction
     * @return the "amount" of the Transaction
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Gets the sender of the Transaction
     * @return the "from" of the Transaction
     */
    public String getFrom() {
        return from;
    }

    /**
     * Gets the unique ID of the Transaction
     * @return the "id" of the Transaction
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the recipient of the Transaction
     * @return the "to" of the Transaction
     */
    public String getTo() {
        return to;
    }

    /**
     * Gets the type of transaction (Transfer, Payment...)
     * @return The "type" of Transaction
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the boolean value of if the Transaction was accepted or declined
     * @return the status of the Transaction
     */
    public boolean getStatus() {
        return transactionStatus;
    }

    private boolean processTransaction(Connection connection) throws SQLException {
        switch (getType()) {

            case "DEPOSIT":
                // Deposits only require 'to' account
                if (getTo() == null) return false;
                Account depositAccount = accountRepository.getAccount(connection, getTo());
                if (depositAccount == null) return false;
                depositAccount.deposit(getAmount());
                accountRepository.updateBalance(connection, depositAccount);
                return true;

            case "WITHDRAWAL":
                // Withdrawals only require 'from' account
                if (getFrom() == null) {
                    logger.info("From is null");
                    return false;
                }

                Account withdrawAccount = accountRepository.getAccount(connection, getFrom());
                if (withdrawAccount == null) {
                    logger.info("account is null");
                    return false;
                }
                try {
                    withdrawAccount.withdraw(getAmount());
                    accountRepository.updateBalance(connection, withdrawAccount);
                    return true;
                } catch (ArithmeticException e) {
                    logger.info("Arithmetic exception");
                    return false;
                }

            case "TRANSFER":
                // Transfers require both 'from' and 'to' accounts
                if (getFrom() == null || getTo() == null) return false;
                Account senderAccount = accountRepository.getAccount(connection, getFrom());
                Account receiverAccount = accountRepository.getAccount(connection, getTo());
                if (senderAccount == null || receiverAccount == null) return false;
                try {
                    senderAccount.withdraw(getAmount());
                    receiverAccount.deposit(getAmount());
                    accountRepository.updateBalance(connection, senderAccount);
                    accountRepository.updateBalance(connection, receiverAccount);
                    return true;
                } catch (ArithmeticException e) {
                    return false;
                }

            case "PAYMENT":
                // Payments require both 'from' and 'to' accounts, similar to transfer
                if (getFrom() == null || getTo() == null) return false;
                Account payerAccount = accountRepository.getAccount(connection, getFrom());
                if (payerAccount == null) return false;
                try {
                    payerAccount.withdraw(getAmount());
                    accountRepository.updateBalance(connection, payerAccount);
                    return true;
                } catch (ArithmeticException e) {
                    return false;
                }

            default:
                logger.info("Unknown transaction type: {}", this);
                return true;
        }
    }

    /**
     * Returns a String interpretation of a Transaction
     * @return A String of the Transaction
     */
    public String toString() {
        return "Transaction [timestamp=" + timestamp + ", amount=" + amount + ", from=" + from + ", id=" + id + ", to=" + to + ", type=" + type + ", transactionStatus=" + transactionStatus + "]";
    }

    private void setBalance(BigDecimal amount) {
        this.amount = amount;
    }
}
