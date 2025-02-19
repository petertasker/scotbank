//package uk.co.asepstrath.bank;
//
//import java.math.BigDecimal;
//
//public class CurrentAccount extends Account {
//
//    private BigDecimal Overdraft_Limit; // Overdraft limit
//
//    public CurrentAccount(Customer customer, BigDecimal startingBalance, BigDecimal Overdraft_Limit) {
//        super(customer, startingBalance);
//        this.Overdraft_Limit = Overdraft_Limit;
//    }
//
//    public BigDecimal getOverdraft_Limit() {
//        return Overdraft_Limit;
//    }
//
//    public void setOverdraft_Limit(BigDecimal Overdraft_Limit) {
//        this.Overdraft_Limit = Overdraft_Limit;
//    }
//
//    @Override
//    public void deposit(BigDecimal amount) {
//        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new ArithmeticException("Deposit amount needs to be greater than 0");
//        }
//        super.deposit(amount);
//    }
//
//    @Override
//    public void withdraw(BigDecimal amount) {
//        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new ArithmeticException("Withdrawal amount needs to be greater than 0");
//        }
//        super.withdraw(amount);
//    }
//}
