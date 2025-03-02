package uk.co.asepstrath.bank;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.joda.time.DateTime;

import java.math.BigDecimal;

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
    @JacksonXmlProperty(localName = "status")
    private boolean transactionStatus;

    public Transaction() {
        // For jackson mapper
    }

    public Transaction(DateTime timestamp, BigDecimal amount, String from, String id, String to, String type, boolean transactionStatus) {
        this.timestamp= timestamp;
        this.amount = amount;
        this.from = from;
        this.id = id;
        this.to = to;
        this.type = type;
        this.transactionStatus = transactionStatus;
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

    /**
     * Returns a String interpretation of a Transaction
     * @return A String of the Transaction
     */
    public String toString() {
        return "Transaction [timestamp=" + timestamp + ", amount=" + amount + ", from=" + from + ", id=" + id + ", to=" + to + ", type=" + type + ", transactionStatus=" + transactionStatus + "]";
    }
}
