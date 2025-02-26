package uk.co.asepstrath.bank;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.jooby.test.JoobyTest;
import io.jooby.test.MockRouter;

import jakarta.inject.Inject;
import org.joda.time.DateTime;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.xml.stream.XMLStreamException;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;

@JoobyTest(App.class)
class DatabaseTests {
    @Inject
    private DataSource dataSource;
    private DatabaseInitialiser databaseInitialiser;
    private Connection connection;

    @BeforeAll
    static void setupClass() {
        new MockRouter(new App());
    }

    @BeforeEach
    void setUp() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");

        dataSource = new HikariDataSource(config);
        connection = dataSource.getConnection();

        assertNotNull(dataSource, "DataSource should not be null");
        databaseInitialiser = new DatabaseInitialiser(dataSource);

        cleanDatabase();
        // Moved databaseInitialiser so the database is cleaned properly
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
        }
    }

    private void cleanDatabase() throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS Transactions");
            statement.execute("DROP TABLE IF EXISTS Accounts");
            statement.execute("DROP TABLE IF EXISTS Businesses");
        }
    }

    @Test
    void testTablesExist() throws SQLException {
        databaseInitialiser.initialise();
        try (var rs = connection.getMetaData().getTables(null, null, "%", null)) {
            var tables = new java.util.ArrayList<String>();
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME").toLowerCase());
            }
            assertTrue(tables.contains("accounts"));
            assertTrue(tables.contains("businesses"));
            assertTrue(tables.contains("transactions"));
        }
    }

    /*
     * Test Coverage for Catch Statements & throwing JsonParse, IO and XMLStreamException
     *    Use Mockito.spy for partial mocking and wrapping the existing instance
     *      This allows us overide the 'Fetch' functions to throw the respective exceptions.
     *      Keeps the functionality of the rest of 'DatabaseInitialiser'.
     */ 

    @Test
    void testInitialiseCatchJsonParseException() throws Exception {
        DatabaseInitialiser spyInitialiser = Mockito.spy(databaseInitialiser);
        doThrow(new JsonParseException("Testing JsonParseException")).when(spyInitialiser).fetchAccounts();
        
        SQLException exception = null;
        try{
            spyInitialiser.initialise();
            fail("SQLException was not thrown.");
        } catch (SQLException e) {
            exception = e;
        }

        assertEquals("Fetching failed somewhere", exception.getMessage());
        assertInstanceOf(JsonParseException.class, exception.getCause());
    }

    @Test
    void testInitialiseCatchXMLStreamException() throws Exception {
        DatabaseInitialiser spyInitialiser = Mockito.spy(databaseInitialiser);
        doThrow(new XMLStreamException("Testing XMLStreamException")).when(spyInitialiser).fetchTransactions();

        SQLException exception = null;
        try{
            spyInitialiser.initialise();
            fail("SQLException was not thrown");
        } catch (SQLException e){
            exception = e;
        }

        assertEquals("Fetching failed somewhere", exception.getMessage());
        assertInstanceOf(XMLStreamException.class, exception.getCause());
    }

    @Test
    void testInitialiseCatchIOException() throws Exception {
        DatabaseInitialiser spyInitialiser = Mockito.spy(databaseInitialiser);
        doThrow(new IOException("Testing IOException")).when(spyInitialiser).fetchBusinesses();

        SQLException exception = null;
        try{
            spyInitialiser.initialise();
            fail("SQLException was not thrown");
        } catch (SQLException e){
            exception = e;
        }

        assertEquals("Database creation failed", exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());
    }

    /* Test Coverage for inserting into tables
       Using Mockito Mock
    */

    @Test
    void testInsertDataInTables() throws Exception {
        Connection mockConnection = Mockito.mock(Connection.class);
        PreparedStatement mockPreparedStatement = Mockito.mock(PreparedStatement.class);
        ResultSet mockResultSet = Mockito.mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("Name")).thenReturn("John Doe");
        when(mockResultSet.getBigDecimal("Balance")).thenReturn(new BigDecimal("1000.00"));
        when(mockResultSet.getBoolean("RoundUpEnabled")).thenReturn(true);

        DatabaseHandler dbHandler = Mockito.spy(new DatabaseHandler());

        // insert into Accounts Table
        Account account = new Account("ABC123","John Doe",new BigDecimal("1000.00"),true);
        dbHandler.insertAccount(mockConnection, account);
        verify(mockConnection).prepareStatement(contains("INSERT INTO Accounts"));
        verify(mockPreparedStatement).setString(1, "ABC123");
        verify(mockPreparedStatement).setBigDecimal(2, new BigDecimal("1000.00"));
        verify(mockPreparedStatement).setString(3, "John Doe");
        verify(mockPreparedStatement).setBoolean(4, true);


        // insert into Business Table

        Business business = new Business("XYZ123","Something", "Retail",false);
        dbHandler.insertBusiness(mockConnection, business);
        verify(mockConnection).prepareStatement(contains("INSERT INTO Businesses"));
        verify(mockPreparedStatement).setString(1, "XYZ123");
        verify(mockPreparedStatement).setString(2, "Something");
        verify(mockPreparedStatement).setString(3, "Retail");
        verify(mockPreparedStatement).setBoolean(4, false);

        // insert into Transaction Table
        DateTime time = new DateTime(2025,5,11,22,20);
        BigDecimal Amount = new BigDecimal("1000.00");
        Transaction transaction =new Transaction(time, Amount,"ABC123","T123","XYZ123","PAYMENT",true);
        dbHandler.insertTransaction(mockConnection, transaction);
        verify(mockConnection).prepareStatement(contains("INSERT INTO Transactions"));
        verify(mockPreparedStatement).setTimestamp(1, new Timestamp(transaction.getTimestamp().getMillis()));
        verify(mockPreparedStatement).setBigDecimal(2, new BigDecimal("1000.00"));
        verify(mockPreparedStatement).setString(3, "ABC123");
        verify(mockPreparedStatement).setString(4, "T123");
        verify(mockPreparedStatement).setString(5, "XYZ123");
        verify(mockPreparedStatement).setString(6, "PAYMENT");
        verify(mockPreparedStatement).setBoolean(7, true);
    }
}