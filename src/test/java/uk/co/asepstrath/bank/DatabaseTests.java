package uk.co.asepstrath.bank;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.jooby.test.JoobyTest;
import io.jooby.test.MockRouter;

import jakarta.inject.Inject;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import javax.sql.DataSource;
import javax.xml.stream.XMLStreamException;

import java.io.IOException;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

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
        databaseHandler = new DatabaseHandler();

        cleanDatabase();
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
        assertTrue(exception.getCause() instanceof JsonParseException);
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
        assertTrue(exception.getCause() instanceof XMLStreamException);
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
        assertTrue(exception.getCause() instanceof IOException);
    }
}
