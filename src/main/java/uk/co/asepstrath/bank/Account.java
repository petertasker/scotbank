package uk.co.asepstrath.bank;

public class Account {

    private int AccBalance;

    public Account() {
        this.AccBalance = 0;
    }

    public Account(int accBalance) {
        this.AccBalance = accBalance;
    }

    public void deposit(int amount) {
        if (amount > 0){
            AccBalance += amount;
        }else{
            System.out.println("Deposit amount needs to be greater than 0");
        }
    }

    public void Withdraw(int amount) throws ArithmeticException{
        if (amount < AccBalance){
            AccBalance -= amount;
        }else if(amount <= 0){
            System.out.println("Withdraw amount needs to be greater than 0");
        } else {
            throw new ArithmeticException("Insufficient Funds: cannot withdrawal amount more than available balance");
        }
    }

    public int getBalance() {
        return AccBalance;
    }

}
