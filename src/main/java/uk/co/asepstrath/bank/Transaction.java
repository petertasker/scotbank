package uk.co.asepstrath.bank;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.joda.time.DateTime;

import java.math.BigDecimal;


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
    private boolean TransactionStatus;

    public Transaction() {
        // For jackson mapper
    }

    public Transaction(DateTime timestamp, BigDecimal amount, String from, String id, String to, String type, boolean TransactionStatus) {
        this.timestamp= timestamp;
        this.amount = amount;
        this.from = from;
        this.id = id;
        this.to = to;
        this.type = type;
        this.TransactionStatus = TransactionStatus;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getFrom() {
        return from;
    }

    public String getId() {
        return id;
    }

    public String getTo() {
        return to;
    }

    public String getType() {
        return type;
    }

    public boolean getStatus() {return TransactionStatus;}
}
