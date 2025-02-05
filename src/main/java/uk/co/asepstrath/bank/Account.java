package uk.co.asepstrath.bank;

import java.math.BigDecimal;

public class Account {

    private BigDecimal balance;
    private String accountName;
    public Account(String accountName, BigDecimal initialBalance) {
        this.balance = initialBalance;
        this.accountName = accountName;
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

    public String toString(){
        return "Account name: " + this.accountName + ", Balance: " + this.balance;
    }


}
