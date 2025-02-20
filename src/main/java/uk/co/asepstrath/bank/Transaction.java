package uk.co.asepstrath.bank;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;
public class Transaction {

    @JacksonXmlProperty(localName = "timestamp")
    private String timestamp;
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

    public Transaction() {
        // For jackson mapper
    }

    public Transaction(String timestamp, int amount, String from, String id, String to, String type) {
        this.timestamp= timestamp;
        this.amount = BigDecimal.valueOf(amount);
        this.from = from;
        this.id = id;
        this.to = to;
        this.type = type;
    }

    public String getTimestamp() {
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
}
