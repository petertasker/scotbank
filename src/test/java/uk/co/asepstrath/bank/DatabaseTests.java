package uk.co.asepstrath.bank;
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

    // Ensure that accounts are being loaded into the database from the api
    @Test
    void testInitialiseAndInsertAccounts() throws SQLException {
        databaseInitialiser.initialise();

        verify(statement).executeUpdate(contains("CREATE TABLE Accounts"));
        verify(statement).executeUpdate(contains("CREATE TABLE Business"));
//
//        verify(preparedStatement, times(2)).executeUpdate();
//        verify(preparedStatement).setString(1, "3232323");
//        verify(preparedStatement).setInt(2, 10);
//        verify(preparedStatement).setBoolean(3, false);
    }
}
