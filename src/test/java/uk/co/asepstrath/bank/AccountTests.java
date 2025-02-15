package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTests {
    /* A new Account should have a value of 0 */
    @Test
    public void createAccount(){
        Account a = new Account(new Customer("John Smith", "johnsmith1@protonmail.com"), BigDecimal.valueOf(15));
        assertNotNull(a);
    }
    
    @Test
    public void deposit(){
        Account a = new Account(new Customer("Jacob", "jacob@protonmail.com"),BigDecimal.valueOf(20));
        a.deposit(BigDecimal.valueOf(50)); // deposit a 50 in a 20
        assertEquals(BigDecimal.valueOf(70),a.getBalance());
    }

    @Test
    public void Withdraw(){
        Account a = new Account(new Customer("Simon", "Simon@yahoo.co.uk"),BigDecimal.valueOf(40));
        a.withdraw(BigDecimal.valueOf(20)); // withdraw 20 pounds
        assertEquals(BigDecimal.valueOf(20), a.getBalance());
    }

    @Test
    public void TestArithmeticException(){
        Account a = new Account(new Customer("John Smith", "JS@jpmorgan.co"),BigDecimal.valueOf(20));
        a.deposit(BigDecimal.valueOf(30));
        Assertions.assertThrows(ArithmeticException.class, () -> a.withdraw(BigDecimal.valueOf(100)));
    }

    @Test
    public void TestArithmeticException2() {
        Account a = new Account(new Customer("John Smith", "john505@gmail.com"),BigDecimal.valueOf(20));
        Assertions.assertThrows(ArithmeticException.class, () -> a.withdraw(BigDecimal.valueOf(-5)));
    }

    @Test
    public void SuperSavingTest(){
        Account a = new Account(new Customer("Michael", "Michael9090@yahoo.co.uk"),BigDecimal.valueOf(20));
        for (int i=0; i<5; i++){
            a.deposit(BigDecimal.valueOf(10));
        }
        for (int i=0; i<3; i++){
            a.withdraw(BigDecimal.valueOf(20));
        }
        assertEquals(BigDecimal.valueOf(10),a.getBalance());
    }

    @Test
    public void TestForPennies(){
        Account a = new Account(new Customer("Jack", "JB@gmail.com"),BigDecimal.valueOf(5.45));
        a.deposit(BigDecimal.valueOf(17.56));
        assertEquals(BigDecimal.valueOf(23.01), a.getBalance());
    }

    @Test
    /* Withdrawing £20 from an account with £40 should result in an account containing £20 */
    void addFunds2() {
        Account a = new Account(new Customer("John Doe", "JohnDoe58@outlook.co.uk"), BigDecimal.valueOf(40));
        a.withdraw(BigDecimal.valueOf(20));
        assertEquals(BigDecimal.valueOf(20), a.getBalance());
    }

    @Test
    /* Withdrawing £100 from an account with £30 should throw an ArithmeticException */
    void illegalWithdraw() {
        Account a = new Account(new Customer("John Doe", "DoeJohn_1@hotmail.co.uk"), BigDecimal.valueOf(30));
        assertThrows(ArithmeticException.class, () -> a.withdraw(BigDecimal.valueOf(100)));
    }

    @Test
    /* Depositing an amount of zero should throw an ArithmeticException */
    void illegalDepositZero() {
        Account a = new Account(new Customer("John Doe", "JD50501039@uni.strath.ac.uk"), BigDecimal.valueOf(20));
        assertThrows(ArithmeticException.class, () -> a.deposit(BigDecimal.valueOf(0)));
    }

    @Test
    /* Depositing a negative amount should throw an ArithmeticException */
    void illegalDepositNegative() {
        Account a = new Account(new Customer("John Doe", "thisismyfakeemail@google.com"), BigDecimal.valueOf(20));
        assertThrows(ArithmeticException.class, () -> a.deposit(BigDecimal.valueOf(-5)));
    }

    @Test
    /* Starting with an account with £20, deposit £10 five times then withdraw £20 three times.
    The account should end with £10*/
    void complexDepositAndWithdraw() {
        Account a = new Account(new Customer("John Doe", "funtimeguy52@fun.club"), BigDecimal.valueOf(20));
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
        Account a = new Account(new Customer("John Doe", "johndoeloverboy@yahoo.co.uk"), BigDecimal.valueOf(5.45));
        a.deposit(BigDecimal.valueOf(17.56));
        assertEquals(BigDecimal.valueOf(23.01), a.getBalance());
    }

    @Test

    void displayData(){
        Account a = new Account(new Customer("John Doe", "crazyjohn215@msn.com"), BigDecimal.valueOf(5.45));
        assertEquals( "Account name: John Doe, Balance: 5.45", a.toString());
    }

    @Test
    void getUserIDTest(){
        Customer customer = new Customer("John Smith", "johnsmith@yahoo.co.uk");
        Account a = customer.createAccount();

        String UserID = a.getCustomer().getUserID();

        Assertions.assertEquals(UserID,customer.getUserID(),"The Account matches from the User ID");
    }
}
