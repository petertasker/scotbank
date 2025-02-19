package uk.co.asepstrath.bank;

import java.math.BigDecimal;
public class Transaction {

    private String timestamp;
    private BigDecimal amount;
    private String from;
    private String id;
    private String to;
    private String type;

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
