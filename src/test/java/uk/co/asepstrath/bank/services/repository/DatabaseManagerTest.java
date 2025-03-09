package uk.co.asepstrath.bank.services.repository;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.Business;
import uk.co.asepstrath.bank.Manager;
import uk.co.asepstrath.bank.Transaction;
import uk.co.asepstrath.bank.services.data.*;
import uk.co.asepstrath.bank.services.login.HashingPasswordService;

import javax.sql.DataSource;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.List;

import static org.mockito.Mockito.*;


class DatabaseManagerTest {
    @Mock
    private Logger log;
    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;
    @Mock
    private Statement statement;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private BusinessRepository businessRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private AccountDataService accountDataService;
    @Mock
    private BusinessDataService businessDataService;
    @Mock
    private TransactionDataService transactionDataService;
    @Mock
    private ManagerDataService managerDataService;
    private DatabaseManager databaseManager;

    @BeforeEach
    void setUp() throws SQLException, NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        when(dataSource.getConnection()).thenReturn(connection);
        databaseManager = new DatabaseManager(dataSource, log);

        // inject mock repositories into a private field
        setPrivateField(databaseManager, "accountRepository", accountRepository);
        setPrivateField(databaseManager,"businessRepository", businessRepository);
        setPrivateField(databaseManager,"transactionRepository", transactionRepository);
        setPrivateField(databaseManager,"managerRepository", managerRepository);

        // inject mock data services into private fields
        setPrivateField(databaseManager,"accountDataService", accountDataService);
        setPrivateField(databaseManager,"businessDataService", businessDataService);
        setPrivateField(databaseManager,"transactionDataService", transactionDataService);
        setPrivateField(databaseManager,"managerDataService", managerDataService);
    }

    // using reflection to create private fields
    private void setPrivateField(Object object, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = databaseManager.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object,value);
    }

    @Test
    void testCreateTables() throws SQLException {
        databaseManager.createTables(connection);

        verify(accountRepository).createTable(connection);
        verify(businessRepository).createTable(connection);
        verify(transactionRepository).createTable(connection);
        verify(managerRepository).createTable(connection);
    }

    @Test
    void testInsertionTables() throws IOException, XMLStreamException, SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        Account accounts = new Account("ABC123","John Doe", new BigDecimal(100),true);
        Business businesses = new Business("B123","Something","Retail",false);
        DateTime date  = new DateTime(2025,5,20,16,20, 0);
        Transaction transactions = new Transaction(date, new BigDecimal(50),"ABC123","T123","B123","PAYMENT",true);
        Manager managers = new Manager("Manager123","Nothing");

        // mock the services
        when(accountDataService.fetchData()).thenReturn(List.of(accounts));
        when(businessDataService.fetchData()).thenReturn(List.of(businesses));
        when(transactionDataService.fetchData()).thenReturn(List.of(transactions));
        when(managerDataService.fetchData()).thenReturn(List.of(managers));

        databaseManager.insertData(connection);

        String accountPassword = DatabaseManager.generatePassword(accounts.getAccountID());
        String managerPassword = DatabaseManager.generateManagerPassword(managers.getManagerID());

        verify(accountRepository).insert(connection, accounts, accountPassword);
        verify(businessRepository).insert(connection, businesses);
        verify(transactionRepository).insert(connection, transactions);
        verify(managerRepository).insert(connection, managers,managerPassword);
    }

    @Test
    void testGeneratePassword() throws NoSuchAlgorithmException {
        String accountId = "Account123";
        String password = DatabaseManager.generatePassword(accountId);

        Assertions.assertNotNull(password);
        Assertions.assertFalse(password.isEmpty());
        Assertions.assertTrue(password.startsWith("Psw@"));
        Assertions.assertTrue(password.endsWith("$$"));
        Assertions.assertEquals(14, password.length());
    }

    @Test
    void testGeneratingManagersPassword() throws NoSuchAlgorithmException {
        String managerId = "Manager123";
        String managerPassword = DatabaseManager.generateManagerPassword(managerId);

        Assertions.assertNotNull(managerPassword);
        Assertions.assertFalse(managerPassword.isEmpty());
        Assertions.assertTrue(managerPassword.startsWith("Manager@"));
        Assertions.assertTrue(managerPassword.endsWith("$$"));
        Assertions.assertEquals(18, managerPassword.length());
    }
}