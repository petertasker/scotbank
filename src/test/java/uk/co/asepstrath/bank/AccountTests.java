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
        Account a = new Account(new BigDecimal(20));
        a.deposit(new BigDecimal(50)); // deposit a 50 in a 20
        Assertions.assertEquals(new BigDecimal(70),a.getBalance());
    }

    @Test
    public void Withdraw(){
        Account a = new Account(new BigDecimal(40));
        a.Withdraw(new BigDecimal(20)); // withdraw 20 pounds
        Assertions.assertEquals(new BigDecimal(20), a.getBalance());
    }

    @Test
    public void TestArithmeticException(){
        Account a = new Account();
        a.deposit(new BigDecimal(30));
        Assertions.assertThrows(ArithmeticException.class, () -> a.Withdraw(new BigDecimal(100)));
    }

    @Test
    public void SuperSavingTest(){
        Account a = new Account(new BigDecimal(20));
        for (int i=0; i<5; i++){
            a.deposit(new BigDecimal(10));
        }
        for (int i=0; i<3; i++){
            a.Withdraw(new BigDecimal(20));
        }
        Assertions.assertEquals(new BigDecimal(10),a.getBalance());
    }

    @Test
    public void Testforpennies(){
        Account a = new Account(new BigDecimal("5.45"));
        a.deposit(new BigDecimal("17.56"));
        Assertions.assertEquals(new BigDecimal("23.01"), a.getBalance());
    }

}
