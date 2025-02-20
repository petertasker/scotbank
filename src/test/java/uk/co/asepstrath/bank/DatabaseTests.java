package uk.co.asepstrath.bank;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DatabaseTests {

    private DataSource dataSource;
    private Connection connection;
    private DatabaseInitialiser databaseInitialiser;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    @BeforeEach
    public void setUp() throws SQLException {
        dataSource = mock(DataSource.class);
        connection = mock(Connection.class);
        statement = mock(Statement.class);
        preparedStatement = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        databaseInitialiser = spy(new DatabaseInitialiser(dataSource));
    }

    //Ensure that accounts are being loaded into the database from the api
    @Test
    void testInitialiseDatabase() throws SQLException {
        databaseInitialiser.initialise();

        verify(statement).executeUpdate(contains("CREATE TABLE Accounts"));
        verify(statement).executeUpdate(contains("CREATE TABLE Business"));
        verify(statement).executeUpdate(contains("CREATE TABLE Transactions"));
//
//        verify(preparedStatement, times(2)).executeUpdate();
//        verify(preparedStatement).setString(1, "3232323");
//        verify(preparedStatement).setInt(2, 10);
//        verify(preparedStatement).setBoolean(3, false);
    }

    @Test
    void InsertIntoTables() throws SQLException {
        DatabaseHandler dbHandler = new DatabaseHandler(dataSource);
        Connection mockConnection = mock(Connection.class);

        // Ensure connection and preparedStatement are mocked
        when(mockConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1); // Simulate successful insert

        // Mock Account, Business, and Transaction objects
        Account account = new Account("A123", "TestUser",new BigDecimal("100.50"),true);
        Business business = new Business("B123", "Test Business", "Retail", false);

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTime timestamp = formatter.parseDateTime("2024-02-20T12:00:00");
        Transaction transaction = new Transaction(timestamp, 50, "A123", "T123", "B123", "Purchase");

        // insert into tables
        dbHandler.insertAccount(mockConnection, account);
        dbHandler.insertBusiness(mockConnection, business);
        dbHandler.insertTransaction(mockConnection, transaction);

        // Verify executeUpdate() was called for each statement
        verify(preparedStatement, times(3)).executeUpdate();
    }

}
