package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class AccountTests {
    /* A new Account should have a value of 0 */
    @Test
    void createAccount(){
        Account a = new Account("1", "John Smith", BigDecimal.valueOf(15), true);
        assertNotNull(a);
    }

    @Test
    void deposit(){
        Account a = new Account("1", "Jacob", BigDecimal.valueOf(20), true);
        a.deposit(BigDecimal.valueOf(50)); // deposit a 50 in a 20
        assertEquals(BigDecimal.valueOf(70),a.getBalance());
    }

    @Test
     void Withdraw(){
        Account a = new Account("1", "Simon", BigDecimal.valueOf(40), false);
        a.withdraw(BigDecimal.valueOf(20)); // withdraw 20 pounds
        assertEquals(BigDecimal.valueOf(20), a.getBalance());
    }

    @Test
     void TestArithmeticException(){
        Account a = new Account("1", "John Smith",BigDecimal.valueOf(20), false);
        a.deposit(BigDecimal.valueOf(30));
        Assertions.assertThrows(ArithmeticException.class, () -> a.withdraw(BigDecimal.valueOf(100)));
    }

    @Test
     void TestArithmeticException2() {
        Account a = new Account("1", "John Smith",BigDecimal.valueOf(20), false);
        Assertions.assertThrows(ArithmeticException.class, () -> a.withdraw(BigDecimal.valueOf(-5)));
    }

    @Test
     void SuperSavingTest(){
        Account a = new Account("2", "Michael", BigDecimal.valueOf(20), false);
        for (int i=0; i<5; i++){
            a.deposit(BigDecimal.valueOf(10));
        }
        for (int i=0; i<3; i++){
            a.withdraw(BigDecimal.valueOf(20));
        }
        assertEquals(BigDecimal.valueOf(10),a.getBalance());
    }

    @Test
     void TestForPennies(){
        Account a = new Account("50", "Jack", BigDecimal.valueOf(5.45), false);
        a.deposit(BigDecimal.valueOf(17.56));
        assertEquals(BigDecimal.valueOf(23.01), a.getBalance());
    }

    @Test
        /* Withdrawing £20 from an account with £40 should result in an account containing £20 */
    void addFunds2() {
        Account a = new Account("4", "John Doe", BigDecimal.valueOf(40), false);
        a.withdraw(BigDecimal.valueOf(20));
        assertEquals(BigDecimal.valueOf(20), a.getBalance());
    }

    @Test
        /* Withdrawing £100 from an account with £30 should throw an ArithmeticException */
    void illegalWithdraw() {
        Account a = new Account("1", "John Doe", BigDecimal.valueOf(30), true);
        assertThrows(ArithmeticException.class, () -> a.withdraw(BigDecimal.valueOf(100)));
    }

    @Test
        /* Depositing an amount of zero should throw an ArithmeticException */
    void illegalDepositZero() {
        Account a = new Account("2", "John Doe", BigDecimal.valueOf(20), false);
        assertThrows(ArithmeticException.class, () -> a.deposit(BigDecimal.valueOf(0)));
    }

    @Test
        /* Depositing a negative amount should throw an ArithmeticException */
    void illegalDepositNegative() {
        Account a = new Account("41", "John Doe", BigDecimal.valueOf(20), false);
        assertThrows(ArithmeticException.class, () -> a.deposit(BigDecimal.valueOf(-5)));
    }

    @Test
    /* Starting with an account with £20, deposit £10 five times then withdraw £20 three times.
    The account should end with £10*/
    void complexDepositAndWithdraw() {
        Account a = new Account("41", "John Doe", BigDecimal.valueOf(20), true);
        for (int i = 0; i < 5; i++) {
            a.deposit(BigDecimal.valueOf(10));
        }
        for (int i = 0; i < 3; i++) {
            a.withdraw(BigDecimal.valueOf(20));
        }
        assertEquals(BigDecimal.valueOf(10), a.getBalance());
    }
    @Test
        /* Depositing £17.56 into an account with £5.45 should result in an account containing £23.01*/
    void floatWithdraw() {
        Account a = new Account("4", "John Doe", BigDecimal.valueOf(5.45), false);
        a.deposit(BigDecimal.valueOf(17.56));
        assertEquals(BigDecimal.valueOf(23.01), a.getBalance());
    }

    @Test
    void displayData(){
        Account a = new Account("4", "John Doe", BigDecimal.valueOf(5.45), false);
        assertEquals("id: 4\nname: John Doe\nbalance: 5.45\nroundUpEnabled: false", a.toString());
    }
}
