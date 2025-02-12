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

    public String toString() {
        return "Account name: " + this.accountName + ", Balance: " + this.balance;
    }


}
