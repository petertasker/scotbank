package uk.co.asepstrath.bank;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static uk.co.asepstrath.bank.Constants.ACCOUNT_OBJECT_MAX_BALANCE;

public class Account {

    private final String accountID;
    private BigDecimal balance;
    private final boolean roundUpEnabled;
    private final String name;

    @JsonCreator
    public Account(
            @JsonProperty("id")  String accountID,
            @JsonProperty("name") String name,
            @JsonProperty("startingBalance") BigDecimal startingBalance,
            @JsonProperty("roundUpEnabled") boolean roundUpEnabled
        ) {
        this.accountID = accountID;
        this.balance = startingBalance;
        this.name = name;
        this.roundUpEnabled = roundUpEnabled;
    }

    public void deposit(BigDecimal amount) throws ArithmeticException {

        // Amount <= 0
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ArithmeticException("Deposit amount needs to be greater than 0");
        }

        // Amount would lead to overflow in SQL database
        if (balance.add(amount).compareTo(ACCOUNT_OBJECT_MAX_BALANCE) > 0) {
            throw new ArithmeticException("Deposit would exceed maximum account balance");
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

    public String getName() {
        return name;
    }

    public boolean isRoundUpEnabled() {
        return roundUpEnabled;
    }

    public String toString() {
        return String.format("id: %s%nname: %s%nbalance: %s%nroundUpEnabled: %s",
                getAccountID(), getName(), getBalance(), isRoundUpEnabled());
    }

    public void updateBalance(BigDecimal balance){
        this.balance = balance;
    }

}
