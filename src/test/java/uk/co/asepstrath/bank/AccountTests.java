package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class AccountTests {

    @Test
    public void createAccount(){
        Account a = new Account();
        Assertions.assertNotNull(a);
    }
    
    @Test
    public void deposit(){
        Account a = new Account("Jacob",BigDecimal.valueOf(20));
        a.deposit(BigDecimal.valueOf(50)); // deposit a 50 in a 20
        Assertions.assertEquals(BigDecimal.valueOf(70),a.getBalance());
    }

    @Test
    public void Withdraw(){
        Account a = new Account("Simon",BigDecimal.valueOf(40));
        a.Withdraw(BigDecimal.valueOf(20)); // withdraw 20 pounds
        Assertions.assertEquals(BigDecimal.valueOf(20), a.getBalance());
    }

    @Test
    public void TestArithmeticException(){
        Account a = new Account();
        a.deposit(BigDecimal.valueOf(30));
        Assertions.assertThrows(ArithmeticException.class, () -> a.Withdraw(BigDecimal.valueOf(100)));
    }

    @Test
    public void SuperSavingTest(){
        Account a = new Account("Michale",BigDecimal.valueOf(20));
        for (int i=0; i<5; i++){
            a.deposit(BigDecimal.valueOf(10));
        }
        for (int i=0; i<3; i++){
            a.Withdraw(BigDecimal.valueOf(20));
        }
        Assertions.assertEquals(BigDecimal.valueOf(10),a.getBalance());
    }

    @Test
    public void Testforpennies(){
        Account a = new Account("Jack",BigDecimal.valueOf(5.45));
        a.deposit(BigDecimal.valueOf(17.56));
        Assertions.assertEquals(BigDecimal.valueOf(23.01), a.getBalance());
    }

}
