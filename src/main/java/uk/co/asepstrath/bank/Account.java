package uk.co.asepstrath.bank;
import java.math.BigDecimal;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Account {

    private String accountID;
    private BigDecimal balance;
    private boolean roundUpEnabled;
    private Customer customer;


    @JsonCreator
    public Account(
            @JsonProperty("customer") Customer customer,
            @JsonProperty("balance") BigDecimal startingBalance
        ) {
        this.customer = customer;
        this.accountID = UUID.randomUUID().toString().replace("-", "");
        this.balance = startingBalance;
        this.roundUpEnabled = false;
    }

    public void deposit(BigDecimal amount) throws ArithmeticException{
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArithmeticException("Deposit amount needs to be greater than 0");
        }
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) throws ArithmeticException {
        if (amount.compareTo(balance) > 0) { // if amount is greater than current Balance throw exception
            throw new ArithmeticException("Insufficient Funds: cannot withdrawal amount more than available balance");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) { // if withdraw amount is less or equal to 0
            throw new ArithmeticException("Withdraw amount must be greater than 0");
        }
        balance = balance.subtract(amount);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getAccountID() {
        return accountID;
    }

    @JsonProperty("customerID")
    public String getCustomerID() {
        return customer.getUserID();
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public void setCustomerID(String customerID) {
        // Do nothing, but keep this here so json does not deserialise
    }

    public Customer getCustomer() {
        return customer;
    }

    public String toString() {
        return "Account name: " + getCustomer().getUserName() + ", Balance: " + getBalance();
    }

    public void UpdateBalance(BigDecimal b){
        this.balance = b;
    }

}
