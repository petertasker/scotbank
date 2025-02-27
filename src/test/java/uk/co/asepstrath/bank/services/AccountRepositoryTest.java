//package uk.co.asepstrath.bank.services;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import io.jooby.test.JoobyTest;
//import jakarta.inject.Inject;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import uk.co.asepstrath.bank.Account;
//import uk.co.asepstrath.bank.App;
//import uk.co.asepstrath.bank.services.repositories.AccountRepository;
//import uk.co.asepstrath.bank.services.repositories.DatabaseManager;
//
//import javax.sql.DataSource;
//import javax.xml.stream.XMLStreamException;
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@JoobyTest(App.class)
//public class AccountRepositoryTest {
//
//    @Inject
//    static DataSource dataSource;
//
//    private static final Logger logger = LoggerFactory.getLogger(AccountRepositoryTest.class);
//
//    @Mock
//    private static Connection connection;
//
//    @Mock
//    private PreparedStatement preparedStatement;
//
//    @Mock
//    private ResultSet resultSet;
//
//    @InjectMocks
//    private AccountRepository accountRepository;
//
//    private Account testAccount;
//
//    @BeforeEach
//    void setUp() {
//        testAccount = new Account("12345", "John Doe", new BigDecimal("100.00"), true);
//    }
//
//    @BeforeAll
//    static void configDatabase() throws SQLException, XMLStreamException, IOException {
//        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
//        config.setUsername("sa");
//        config.setPassword("");
//
//        dataSource = new HikariDataSource(config);
//        connection = dataSource.getConnection();
//
//        assertNotNull(dataSource, "DataSource should not be null");
//        DatabaseManager databaseManager = new DatabaseManager(dataSource, logger);
//        databaseManager.initialise();
//    }
//
//
//    @Test
//    void testInsertAccount() throws SQLException {
//        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
//
//        accountRepository.insert(connection, testAccount);
//
//        verify(preparedStatement).setString(1, testAccount.getAccountID());
//        verify(preparedStatement).setBigDecimal(2, testAccount.getBalance());
//        verify(preparedStatement).setString(3, testAccount.getName());
//        verify(preparedStatement).setBoolean(4, testAccount.isRoundUpEnabled());
//        verify(preparedStatement).executeUpdate();
//    }
//
//    @Test
//    void testUpdateBalance() throws SQLException {
//        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
//
//        accountRepository.updateBalance(connection, testAccount);
//
//        verify(preparedStatement).setBigDecimal(1, testAccount.getBalance());
//        verify(preparedStatement).setString(2, testAccount.getAccountID());
//        verify(preparedStatement).executeUpdate();
//    }
//
//    @Test
//    void testGetAccount() throws SQLException {
//        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
//        when(preparedStatement.executeQuery()).thenReturn(resultSet);
//        when(resultSet.next()).thenReturn(true);
//        when(resultSet.getBigDecimal("Balance")).thenReturn(testAccount.getBalance());
//        when(resultSet.getString("Name")).thenReturn(testAccount.getName());
//        when(resultSet.getBoolean("RoundUpEnabled")).thenReturn(testAccount.isRoundUpEnabled());
//
//        Account retrievedAccount = accountRepository.getAccount(connection, "12345");
//
//        assertNotNull(retrievedAccount);
//        assertEquals(testAccount.getAccountID(), retrievedAccount.getAccountID());
//        assertEquals(testAccount.getBalance(), retrievedAccount.getBalance());
//        assertEquals(testAccount.getName(), retrievedAccount.getName());
//        assertEquals(testAccount.isRoundUpEnabled(), retrievedAccount.isRoundUpEnabled());
//    }
//
//    @Test
//    void testGetAccountNotFound() throws SQLException {
//        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
//        when(preparedStatement.executeQuery()).thenReturn(resultSet);
//        when(resultSet.next()).thenReturn(false);
//
//        Account retrievedAccount = accountRepository.getAccount(connection, "99999");
//
//        assertNull(retrievedAccount);
//    }
//
//
//
//
//}
