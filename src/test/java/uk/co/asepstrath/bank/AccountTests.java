package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountTests {

    @Test
    public void createAccount(){
        Account a = new Account();
        Assertions.assertNotNull(a);
    }

    @Test
    public void deposit(){
        Account a = new Account(20);
        a.deposit(50); // deposit a 50 in a 20
        Assertions.assertEquals(70,a.getBalance());
    }

    @Test
    public void Withdraw(){
        Account a = new Account(40);
        a.Withdraw(20); // withdraw 20 pounds
        Assertions.assertEquals(20, a.getBalance());
    }

    @Test
    public void TestArithmeticException(){
        Account a = new Account();
        a.deposit(30);
        Assertions.assertThrows(ArithmeticException.class, () -> a.Withdraw(100));
    }

    @Test
    public void SuperSavingTest(){
        Account a = new Account(20);
        for (int i=0; i<5; i++){
            a.deposit(10);
        }
        for (int i=0; i<3; i++){
            a.Withdraw(20);
        }
        Assertions.assertEquals(10,a.getBalance());
    }


}
