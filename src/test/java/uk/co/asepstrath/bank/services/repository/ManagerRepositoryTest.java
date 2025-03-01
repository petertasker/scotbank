package uk.co.asepstrath.bank.services.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.Manager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new ManagerRepository(mockLogger);
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
    void testInsertManager() throws SQLException {
        // Arrange
        Manager manager = new Manager("M123", "John Doe");
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Act
        repository.insert(mockConnection, manager);

        // Assert
        verify(mockConnection).prepareStatement(eq("INSERT INTO Managers (ManagerID, Name) VALUES (?, ?)"));
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
        assertEquals("A001", accounts.get(0).getAccountID());
        assertEquals("Alice", accounts.get(0).getName());
        assertEquals(new BigDecimal("1000.00"), accounts.get(0).getBalance());
        assertTrue(accounts.get(0).isRoundUpEnabled());

        // Second account
        assertEquals("A002", accounts.get(1).getAccountID());
        assertEquals("Bob", accounts.get(1).getName());
        assertEquals(new BigDecimal("2000.00"), accounts.get(1).getBalance());
        assertFalse(accounts.get(1).isRoundUpEnabled());

        // Verify SQL query
        verify(mockStatement).executeQuery("SELECT AccountID, Name, Balance, RoundUpEnabled FROM Accounts");
    }

    @Test
    void testGetAllAccountsWithSQLException() throws SQLException {
        // Arrange
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenThrow(new SQLException("Database error"));

        // Act
        List<Account> accounts = repository.getAllAccounts(mockConnection);

        // Assert
        assertTrue(accounts.isEmpty());
    }
}