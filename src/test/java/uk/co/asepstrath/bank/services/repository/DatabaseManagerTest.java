package uk.co.asepstrath.bank.services.repository;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.Business;
import uk.co.asepstrath.bank.Manager;
import uk.co.asepstrath.bank.Transaction;
import uk.co.asepstrath.bank.services.data.DataService;

import javax.sql.DataSource;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

import static org.mockito.Mockito.*;


class DatabaseManagerTest {
    private DatabaseManager databaseManager;
    private DataSource mockDataSource;
    private Connection mockConnection;
    private ResultSet mockResultSet;
    private PreparedStatement mockPreparedStatement;
    private Logger mockLogger;

    private AccountRepository mockAccountRepository;
    private BusinessRepository mockBusinessRepository;
    private TransactionRepository mockTransactionRepository;
    private ManagerRepository mockManagerRepository;

    private DataService<Account> mockAccountDataService;
    private DataService<Business> mockBusinessDataService;
    private DataService<Transaction> mockTransactionDataService;
    private DataService<Manager> mockManagerDataService;

    @BeforeEach
    void setUp() throws SQLException {
        mockDataSource = mock(DataSource.class);
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        mockLogger = mock(Logger.class);

        mockAccountRepository = mock(AccountRepository.class);
        mockBusinessRepository = mock(BusinessRepository.class);
        mockTransactionRepository = mock(TransactionRepository.class);
        mockManagerRepository = mock(ManagerRepository.class);

        mockAccountDataService = mock(DataService.class);
        mockBusinessDataService = mock(DataService.class);
        mockTransactionDataService = mock(DataService.class);
        mockManagerDataService = mock(DataService.class);

        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        //when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getBigDecimal("Balance")).thenReturn(new BigDecimal("100"));
        when(mockResultSet.getString("Name")).thenReturn("John Doe");
        when(mockResultSet.getBoolean("RoundUpEnabled")).thenReturn(false);

        databaseManager = new DatabaseManager(mockDataSource, mockLogger);

    }

//    @Test
//    void testForCreatingAndInsertingIntoTables() throws SQLException, XMLStreamException, IOException {
//        // Make sure mockConnection is properly returning from dataSource
//        when(mockDataSource.getConnection()).thenReturn(mockConnection);
//
//        // Setup data mocks
//        when(mockAccountDataService.fetchData()).thenReturn(List.of(new Account("XYZ123","John Doe", new BigDecimal(100), false)));
//        when(mockBusinessDataService.fetchData()).thenReturn(List.of(new Business("B123","Something","Retail",false)));
//        DateTime dateTime = new DateTime(2025, 2, 14, 8, 30, 0);
//        when(mockTransactionDataService.fetchData()).thenReturn(List.of(new Transaction(dateTime,new BigDecimal(50),"XYZ123","T123","B123","PAYMENT",true)));
//        when(mockManagerDataService.fetchData()).thenReturn(List.of(new Manager("M123","Manager1")));
//
//        // Execute method under test
//        databaseManager.initialise();
//
//        // Verify repository methods were called
//        verify(mockAccountRepository).createTable(mockConnection);
//        verify(mockBusinessRepository).createTable(mockConnection);
//        verify(mockTransactionRepository).createTable(mockConnection);
//        verify(mockManagerRepository).createTable(mockConnection);
//
//        // Verify logging happened
//        verify(mockLogger).info("Account table created");
//        verify(mockLogger).info("Business table created");
//        verify(mockLogger).info("Transaction table created");
//        verify(mockLogger).info("Manager table created");
//    }
}