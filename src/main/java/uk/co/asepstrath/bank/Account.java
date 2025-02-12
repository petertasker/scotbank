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

    public Account(String Account_Name, BigDecimal accBalance) {
        this.AccBalance = accBalance;
        this.AccName = Account_Name;
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0){
            AccBalance = AccBalance.add(amount);
        }else{
            System.out.println("Deposit amount needs to be greater than 0");
        }
    }

    public void Withdraw(BigDecimal amount) throws ArithmeticException{
        if (amount.compareTo(AccBalance) > 0){ // if amount is greater than current Balance throw exception
            throw new ArithmeticException("Insufficient Funds: cannot withdrawal amount more than available balance");
        }else if(amount.compareTo(BigDecimal.ZERO) <= 0){ // if withdral amount is less or equal to 0
            System.out.println("Withdraw amount needs to be greater than 0");
        } else {
           AccBalance = AccBalance.subtract(amount);
        }
    }

    public BigDecimal getBalance() {
        return AccBalance;
    }


    public BigDecimal getBalance() {
        return balance;
    }

    public String toString(){
        return "Account name: " + this.accountName + ", Balance: " + this.balance;
    }


}
