package uk.co.asepstrath.bank;

import java.math.BigDecimal;
/** BigDecimals
 * -1 = first value (AccBal) < than second value (amount)
 *  0 = first value (AccBal) = than second value (amount)
 *  1 = first value (AccBal) > than second value (amount)
 */

public class Account {

    private BigDecimal AccBalance;

    public Account() {
        this.AccBalance = BigDecimal.ZERO;
    } // initialise to 0.00

    public Account(BigDecimal accBalance) {
        this.AccBalance = accBalance;
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

}
