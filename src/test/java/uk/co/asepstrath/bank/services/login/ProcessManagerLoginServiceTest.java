package uk.co.asepstrath.bank.services.login;

import io.jooby.Context;
import io.jooby.Session;
import io.jooby.ValueNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static uk.co.asepstrath.bank.Constants.*;

class ProcessManagerLoginServiceTest {

    @Mock
    private Logger logger;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private Context context;

    @Mock
    private Session session;

    private ProcessManagerLoginService processManagerLoginService;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        processManagerLoginService = new ProcessManagerLoginService(dataSource, logger);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(context.session()).thenReturn(session);
    }

    @Test
    void testProcessLoginAccountNotFound() throws SQLException {
        ValueNode value = mock(ValueNode.class);
        when(context.form("managerid")).thenReturn(value);
        when(value.valueOrNull()).thenReturn("funfun123"); // Non existent in DB

        ValueNode valuePassword = mock(ValueNode.class);
        when(context.form("password")).thenReturn(valuePassword);
        when(valuePassword.valueOrNull()).thenReturn("");

        when(resultSet.next()).thenReturn(false);
        processManagerLoginService.processManagerLogin(context);

        verify(session).put(SESSION_ERROR_MESSAGE, "Invalid account ID.");
        verify(context).sendRedirect(ROUTE_MANAGER + ROUTE_LOGIN);
    }

    @Test
    void testProcessLoginEmptyAccountId() throws SQLException {
        ValueNode valueNode = mock(ValueNode.class);
        when(context.form("managerid")).thenReturn(valueNode);
        when(valueNode.valueOrNull()).thenReturn("");

        ValueNode valuePassword = mock(ValueNode.class);
        when(context.form("password")).thenReturn(valuePassword);
        when(valuePassword.valueOrNull()).thenReturn("");

        processManagerLoginService.processManagerLogin(context);

        verify(session).put(SESSION_ERROR_MESSAGE, "Account ID cannot be empty.");
        verify(context).sendRedirect(ROUTE_MANAGER + ROUTE_LOGIN);

        // Verify no database interaction
        verify(dataSource, never()).getConnection();
    }

    @Test
    void testProcessLoginNullValue() throws SQLException {
        ValueNode valueNode = mock(ValueNode.class);
        when(context.form("managerid")).thenReturn(valueNode);
        when(valueNode.valueOrNull()).thenReturn(null);

        ValueNode valuePassword = mock(ValueNode.class);
        when(context.form("password")).thenReturn(valuePassword);
        when(valuePassword.valueOrNull()).thenReturn("mypassword");

        processManagerLoginService.processManagerLogin(context);

        verify(session).put(SESSION_ERROR_MESSAGE, "Account ID cannot be empty.");
        verify(context).sendRedirect(ROUTE_MANAGER + ROUTE_LOGIN);

        verify(dataSource, never()).getConnection();
    }

//    @Test
//    void testProcessLoginSuccess() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
//        String managerId = "admin0";
//        String name = "Peter Tasker";
//        String password = "petertasker";
//
//        // Mock the context and input values
//        ValueNode valueNode = mock(ValueNode.class);
//        when(context.form("managerid")).thenReturn(valueNode);
//        when(valueNode.valueOrNull()).thenReturn(managerId);
//
//        ValueNode valuePassword = mock(ValueNode.class);
//        when(context.form("password")).thenReturn(valuePassword);
//        when(valuePassword.valueOrNull()).thenReturn(password);
//
//        // Mock database interaction
//        when(resultSet.next()).thenReturn(true);
//        when(resultSet.getString("ManagerID")).thenReturn(managerId);
//        when(resultSet.getString("Name")).thenReturn(name);
//        when(resultSet.getString("Password")).thenReturn(password);
//
//        // Mock the HashingPasswordService to bypass hashing
//        HashingPasswordService hashingPasswordService = mock(HashingPasswordService.class);
//
//        // Execute the method
//        ModelAndView<Map<String, Object>> result = processManagerLoginService.processManagerLogin(context);
//
//        // Verify expected result
//        assertNull(result);
//
//        // Verify session updates
//        verify(session).put(SESSION_MANAGER_ID, managerId);
//        verify(session).put(SESSION_MANAGER_NAME, name);
//
//        // Verify database interaction
//        verify(dataSource).getConnection();
//        verify(preparedStatement).setString(1, managerId);
//        verify(preparedStatement).executeQuery();
//    }
//
//
//    @Test
//    void testProcessLoginDatabaseError() throws SQLException {
//        ValueNode valueNode = mock(ValueNode.class);
//        when(context.form("managerid")).thenReturn(valueNode);
//        when(valueNode.valueOrNull()).thenReturn("myId");
//
//        ValueNode valuePassword = mock(ValueNode.class);
//        when(context.form("password")).thenReturn(valuePassword);
//        when(valuePassword.valueOrNull()).thenReturn("mypassword");
//
//        when(dataSource.getConnection()).thenThrow(new SQLException("Database error!!"));
//
//        // Assert that the correct exception is thrown
//        StatusCodeException exception = assertThrows(StatusCodeException.class, () -> {
//            processManagerLoginService.processManagerLogin(context);
//        });
//
//        // Verify exception properties
//        assertEquals(StatusCode.SERVER_ERROR, exception.getStatusCode());
//        assertEquals("A database error occurred", exception.getMessage());
//
//        // Verify that an error was logged
//        verify(logger).error(eq("Database error during manager login"), any(SQLException.class));
//    }

}
