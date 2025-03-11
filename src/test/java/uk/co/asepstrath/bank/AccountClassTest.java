/**
 * Unit testing for the account Class
 */

package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountClassTest {
    /* A new Account should have a value of 0 */
    @Test
    void createAccount() {
        Account a = new Account("1", "John Smith", BigDecimal.valueOf(15), true, new Card("123", "345"));
        assertNotNull(a);
    }

    @Test
    void deposit() {
        Account a = new Account("1", "Jacob", BigDecimal.valueOf(20), true, new Card("123", "345"));
        a.deposit(BigDecimal.valueOf(50)); // deposit a 50 in a 20
        assertEquals(BigDecimal.valueOf(70), a.getBalance());
    }

    @Test
    void Withdraw() {
        Account a = new Account("1", "Simon", BigDecimal.valueOf(40), false, new Card("123", "345"));
        a.withdraw(BigDecimal.valueOf(20)); // withdraw 20 pounds
        assertEquals(BigDecimal.valueOf(20), a.getBalance());
    }

    @Test
    void TestArithmeticException() {

        assertDoesNotThrow(() -> new Account("1", "John Smith", BigDecimal.valueOf(20), false, new Card("123", "345")));
        Account a = new Account("1", "John Smith", BigDecimal.valueOf(20), false, new Card("123", "345"));

        a.deposit(BigDecimal.valueOf(30));
        BigDecimal amount = BigDecimal.valueOf(100);
        assertThrows(ArithmeticException.class, () -> a.withdraw(amount));
    }

    @Test
    void TestArithmeticException2() {
        assertDoesNotThrow(() -> new Account("1", "John Smith", BigDecimal.valueOf(20), false, new Card("123", "345")));
        Account a = new Account("1", "John Smith", BigDecimal.valueOf(20), false, new Card("123", "345"));
        BigDecimal amount = BigDecimal.valueOf(-5);
        assertThrows(ArithmeticException.class, () -> a.withdraw(amount));
    }

    @Test
    void SuperSavingTest() {
        Account a = new Account("2", "Michael", BigDecimal.valueOf(20), false, new Card("123", "345"));
        for (int i = 0; i < 5; i++) {
            a.deposit(BigDecimal.valueOf(10));
        }
        for (int i = 0; i < 3; i++) {
            a.withdraw(BigDecimal.valueOf(20));
        }
        assertEquals(BigDecimal.valueOf(10), a.getBalance());
    }

    @Test
    void TestForPennies() {
        Account a = new Account("50", "Jack", BigDecimal.valueOf(5.45), false, new Card("123", "345"));
        a.deposit(BigDecimal.valueOf(17.56));
        assertEquals(BigDecimal.valueOf(23.01), a.getBalance());
    }

    @Test
        /* Withdrawing £20 from an account with £40 should result in an account containing £20 */
    void addFunds2() {
        Account a = new Account("4", "John Doe", BigDecimal.valueOf(40), false, new Card("123", "345"));
        a.withdraw(BigDecimal.valueOf(20));
        assertEquals(BigDecimal.valueOf(20), a.getBalance());
    }

    @Test
        /* Withdrawing £100 from an account with £30 should throw an ArithmeticException */
    void illegalWithdraw() {
        assertDoesNotThrow(() -> new Account("1", "John Doe", BigDecimal.valueOf(30), true, new Card("123", "345")));
        Account a = new Account("1", "John Doe", BigDecimal.valueOf(30), true, new Card("123", "345"));
        BigDecimal amount = BigDecimal.valueOf(100);
        assertThrows(ArithmeticException.class, () -> a.withdraw(amount));
    }

    @Test
        /* Depositing an amount of zero should throw an ArithmeticException */
    void illegalDepositZero() {
        assertDoesNotThrow(() -> new Account("2", "John Doe", BigDecimal.valueOf(20), false, new Card("123", "345")));
        Account a = new Account("2", "John Doe", BigDecimal.valueOf(20), false, new Card("123", "345"));
        BigDecimal amount = BigDecimal.valueOf(-12);
        assertThrows(ArithmeticException.class, () -> a.deposit(amount));
    }

    @Test
        /* Depositing a negative amount should throw an ArithmeticException */
    void illegalDepositNegative() {
        assertDoesNotThrow(() -> new Account("41", "John Doe", BigDecimal.valueOf(20), false, new Card("123", "345")));
        Account a = new Account("41", "John Doe", BigDecimal.valueOf(20), false, new Card("123", "345"));
        BigDecimal amount = BigDecimal.valueOf(-5);
        assertThrows(ArithmeticException.class, () -> a.deposit(amount));
    }

    @Test
    void updatingBalanceTest() {
        Account a = new Account("1", "John Smith", BigDecimal.valueOf(20), false, new Card("123", "345"));
        BigDecimal bal = BigDecimal.valueOf(10);
        a.updateBalance(bal);
        assertEquals(BigDecimal.valueOf(10), a.getBalance());
    }

    @Test
    /* Starting with an account with £20, deposit £10 five times then withdraw £20 three times.
    The account should end with £10*/
    void complexDepositAndWithdraw() {
        Account a = new Account("41", "John Doe", BigDecimal.valueOf(20), true, new Card("123", "345"));
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
        Account a = new Account("4", "John Doe", BigDecimal.valueOf(5.45), false, new Card("123", "345"));
        a.deposit(BigDecimal.valueOf(17.56));
        assertEquals(BigDecimal.valueOf(23.01), a.getBalance());
    }

    @Test
    void displayData() {
        Account a = new Account("4", "John Doe", BigDecimal.valueOf(5.45), false, new Card("123", "345"));
        String expected = "id: 4" + System.lineSeparator() +
                "name: John Doe" + System.lineSeparator() +
                "balance: 5.45" + System.lineSeparator() +
                "roundUpEnabled: false";
        assertEquals(expected, a.toString());
    }

    @Test
    void illegalOverflowDeposit() {
        Account a = new Account("4", "John Doe", BigDecimal.valueOf(20), false, new Card("123", "345"));
        BigDecimal bal = BigDecimal.valueOf(999999999);
        assertThrows(ArithmeticException.class, () -> a.deposit(bal));

    }
}
