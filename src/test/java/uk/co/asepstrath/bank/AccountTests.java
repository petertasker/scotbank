package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTests {
    /* A new Account should have a value of 0 */
    @Test
    void createAccount() {
        Account a = new Account(BigDecimal.ZERO);
        assertNotNull(a);
    }

    /* Depositing £50 in an account with £20 should result in an account containing £70 */
    @Test
    void addFunds() {
        Account a = new Account(BigDecimal.valueOf(20));

        a.deposit(BigDecimal.valueOf(50));
        assertEquals(BigDecimal.valueOf(70), a.getBalance());
    }

    @Test
    /* Withdrawing £20 from an account with £40 should result in an account containing £20 */
    void addFunds2() {
        Account a = new Account(BigDecimal.valueOf(40));
        a.withdraw(BigDecimal.valueOf(20));
        assertEquals(BigDecimal.valueOf(20), a.getBalance());
    }

    @Test
    /* Withdrawing £100 from an account with £30 should throw an ArithmeticException */
    void illegalWithdraw() {
        Account a = new Account(BigDecimal.valueOf(30));
        assertThrows(ArithmeticException.class, () -> a.withdraw(BigDecimal.valueOf(100)));
    }

    @Test
    /* Starting with an account with £20, deposit £10 five times then withdraw £20 three times.
    The account should end with £10*/
    void complexDepositAndWithdraw() {
        Account a = new Account(BigDecimal.valueOf(20));
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
        Account a = new Account(BigDecimal.valueOf(5.45));
        a.deposit(BigDecimal.valueOf(17.56));
        assertEquals(BigDecimal.valueOf(23.01), a.getBalance());
    }
}
