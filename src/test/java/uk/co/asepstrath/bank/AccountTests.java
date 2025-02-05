package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountTests {

    @Test
    public void createAccount() {
        Account a = new Account();
        Assertions.assertTrue(a != null);
    }

    @Test
    public void addFunds() {
        Account a = new Account();
        a.deposit(20);

        a.deposit(50);
        Assertions.assertEquals(70, a.getBalance());
    }

    @Test
    public void addFunds2() {
        Account a = new Account();
        a.deposit(40);
        a.withdraw(20);
        Assertions.assertEquals(20, a.getBalance());
    }
}
