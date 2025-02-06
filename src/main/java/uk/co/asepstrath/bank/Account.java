package uk.co.asepstrath.bank;

import java.math.BigDecimal;
import java.util.UUID;

public class Account {

    private BigDecimal balance;
    private String accountName;
    private boolean roundUpEnabled;

    public Account(String accountName, BigDecimal startingBalance) {
        String userID = UUID.randomUUID().toString(); // Generate unique identifier for each customer
        this.accountName = accountName;
        this.balance = startingBalance;
    }


    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }


    public void withdraw(BigDecimal amount) {
        // If money withdrawn is greater than amount in account
        if (amount.compareTo(balance) > 0) {
            throw new ArithmeticException("Insufficient funds");
        }
        balance = balance.subtract(amount);
    }


    public BigDecimal getBalance() {
        return balance;
    }

}
