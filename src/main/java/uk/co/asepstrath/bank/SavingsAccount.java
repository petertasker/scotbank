package uk.co.asepstrath.bank;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;


public class SavingsAccount extends Account{
    private static final BigDecimal tierOneLimit = new BigDecimal("2500");
    private static final BigDecimal tierTwoLimit = new BigDecimal("100000");
    private static final BigDecimal rateOne = new BigDecimal("0.01");
    private static final BigDecimal rateTwo = new BigDecimal("0.02");

   private static int withdrawalsNum = 3;

    private int currentMonth;

    private int withdrawalCounter =0;
    public SavingsAccount(Customer customer, BigDecimal startingBalance) {
        super(customer, startingBalance);
        this.currentMonth = LocalDate.now().getMonthValue();
    }

    private void UpdateWithdrawalCounter(){
        int month = LocalDate.now().getMonthValue();
        if (month != currentMonth) {
            withdrawalCounter = 0;
            currentMonth = month;
        }
    }

    private BigDecimal calculateInterest() {
        if (getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal interest = BigDecimal.ZERO;

        BigDecimal tierOneAmount = getBalance().min(tierOneLimit);
        interest = interest.add(tierOneAmount.multiply(rateOne));

        if (getBalance().compareTo(tierOneLimit) > 0) {
            BigDecimal tierTwoAmount = getBalance().subtract(tierOneLimit).min(tierTwoLimit.subtract(tierOneLimit));
            interest = interest.add(tierTwoAmount.multiply(rateTwo));
        }

        return interest.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void withdraw(BigDecimal amount) throws ArithmeticException {
        withdrawalCounter++;
        if(withdrawalCounter > withdrawalsNum){
            throw new ArithmeticException("You have reached the withdrawal limit of this month");
        }
        else {
            UpdateBalance(getBalance().subtract(amount));
        }
    }


}


