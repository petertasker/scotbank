package uk.co.asepstrath.bank.services.repository;

import io.jooby.exception.StatusCodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.DataAccessException;
import uk.co.asepstrath.bank.Manager;
import uk.co.asepstrath.bank.services.login.HashingPasswordService;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ManagerRepositoryTest {

    @Mock
    private Logger mockLogger;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private Statement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private ManagerRepository repository;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        repository = new ManagerRepository(mockLogger);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    }

    @Test
    void testCreateTable() throws SQLException {
        // Arrange
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        // Act
        repository.createTable(mockConnection);

        // Assert
        verify(mockConnection).prepareStatement(contains("CREATE TABLE Managers"));
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testInsertManager() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        // Arrange
        Manager manager = new Manager("M123", "John Doe");
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);


        // Act
        String managerPsw = "SamplePassword";
        String hashedManagerPassword = HashingPasswordService.hashPassword(managerPsw);
        repository.insert(mockConnection, manager, hashedManagerPassword);

        // Assert
        verify(mockConnection).prepareStatement("INSERT INTO Managers (ManagerID, Name, Password) VALUES (?, ?, ?)");

        verify(mockPreparedStatement).setString(1, "M123");
        verify(mockPreparedStatement).setString(2, "John Doe");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testGetAllAccounts() throws SQLException {
        // Arrange
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

        // Mock ResultSet behavior for two accounts
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("AccountID")).thenReturn("A001", "A002");
        when(mockResultSet.getString("Name")).thenReturn("Alice", "Bob");
        when(mockResultSet.getBigDecimal("Balance")).thenReturn(new BigDecimal("1000.00"), new BigDecimal("2000.00"));
        when(mockResultSet.getBoolean("RoundUpEnabled")).thenReturn(true, false);

        // Act
        List<Account> accounts = repository.getAllAccounts(mockConnection);

        // Assert
        assertEquals(2, accounts.size());

        // First account
        assertEquals("A001", accounts.getFirst().getAccountID());
        assertEquals("Alice", accounts.getFirst().getName());
        assertEquals(new BigDecimal("1000.00"), accounts.getFirst().getBalance());
        assertTrue(accounts.get(0).isRoundUpEnabled());

        // Second account
        assertEquals("A002", accounts.get(1).getAccountID());
        assertEquals("Bob", accounts.get(1).getName());
        assertEquals(new BigDecimal("2000.00"), accounts.get(1).getBalance());
        assertFalse(accounts.get(1).isRoundUpEnabled());

        // Verify SQL query
        verify(mockStatement).executeQuery(
                "SELECT AccountID, Name, Balance, RoundUpEnabled, CardNumber, CardCVV FROM Accounts");
    }

    @Test
    void testGetTop10Spenders() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getString("Name")).thenReturn("Alice", "Bob");
        when(mockResultSet.getString("Postcode")).thenReturn("12345", "67890");
        when(mockResultSet.getBigDecimal("TotalAmount")).thenReturn(new BigDecimal("1000.00"),
                new BigDecimal("2000.00"));

        List<Map<String, Object>> spenders = repository.getTopTenSpenders(mockConnection);
        assertEquals(2, spenders.size());

        assertEquals("Alice", spenders.getFirst().get("Name"));
        assertEquals("12345", spenders.getFirst().get("Postcode"));
        assertEquals("1,000.00", spenders.getFirst().get("TotalAmount"));

        assertEquals("Bob", spenders.get(1).get("Name"));
        assertEquals("67890", spenders.get(1).get("Postcode"));
        assertEquals("2,000.00", spenders.get(1).get("TotalAmount"));
    }

    @Test
    void testTop10SpendersDefaultCurrency() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("Name")).thenReturn("Alice");
        when(mockResultSet.getString("Postcode")).thenReturn("12345");
        when(mockResultSet.getBigDecimal("TotalAmount")).thenReturn(null);

        List<Map<String, Object>> spendings = repository.getTopTenSpenders(mockConnection);

        assertEquals(1, spendings.size());
        assertEquals("£0.00", spendings.getFirst().get("TotalAmount"));
    }

    @Test
    void testGetAllAccountsWithSQLException() throws SQLException {
        // Arrange
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenThrow(new SQLException("Database error"));

        assertThrows(StatusCodeException.class, () -> repository.getAllAccounts(mockConnection));
    }

    @Test
    void testGetTop10SpendersWithSQLException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        assertThrows(StatusCodeException.class, () -> repository.getTopTenSpenders(mockConnection));
    }
}