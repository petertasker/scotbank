package uk.co.asepstrath.bank.services.login;

import io.jooby.Context;
import io.jooby.ModelAndView;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static uk.co.asepstrath.bank.Constants.*;

public class ProcessManagerLoginServiceTest {

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

        processManagerLoginService.processManagerLogin(context);

        verify(session).put(SESSION_ERROR_MESSAGE, "Account ID cannot be empty.");
        verify(context).sendRedirect(ROUTE_MANAGER + ROUTE_LOGIN);

        verify(dataSource, never()).getConnection();
    }

    @Test
    void testProcessLoginSuccess() throws SQLException {
        String managerId = "admin0";
        String name = "Peter Tasker";

        ValueNode valueNode = mock(ValueNode.class);
        when(context.form("managerid")).thenReturn(valueNode);
        when(valueNode.valueOrNull()).thenReturn(managerId);

        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("ManagerID")).thenReturn(managerId);
        when(resultSet.getString("Name")).thenReturn(name);

        ModelAndView<Map<String, Object>> result = processManagerLoginService.processManagerLogin(context);

        assertNull(result);

        verify(session).put(SESSION_MANAGER_ID, managerId);
        verify(session).put(SESSION_MANAGER_NAME, name);

        verify(dataSource).getConnection();
        verify(preparedStatement).setString(1, managerId);
        verify(preparedStatement).executeQuery();
    }

    @Test
    void testProcessLoginDatabaseError() throws SQLException {
        ValueNode valueNode = mock(ValueNode.class);
        when(context.form("managerid")).thenReturn(valueNode);
        when(valueNode.valueOrNull()).thenReturn("myId");

        when(dataSource.getConnection()).thenThrow(new SQLException("Database error!!"));

        processManagerLoginService.processManagerLogin(context);

        // Verify that redirect was called with the expected route
        verify(context).sendRedirect(ROUTE_MANAGER + ROUTE_LOGIN);

        // Verify that an error was logged
        verify(logger).error(anyString(), any(SQLException.class));
    }

}
