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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static uk.co.asepstrath.bank.Constants.*;

public class ProcessLoginServiceTest {
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

    private ProcessLoginService service;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        service = new ProcessLoginService(dataSource, logger);

        // Setup common mocks
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(context.session()).thenReturn(session);
    }

    @Test
    void testProcessLoginAccountNotFound() throws SQLException {
        ValueNode value = mock(ValueNode.class);
        when(context.form("accountid")).thenReturn(value);
        when(value.valueOrNull()).thenReturn("funfun123"); // Non-existent in DB

        when(resultSet.next()).thenReturn(false);

        service.processLogin(context);

        verify(session).put(SESSION_ERROR_MESSAGE, "Invalid account ID.");
        verify(context).sendRedirect(ROUTE_LOGIN);
    }

    @Test
    void testProcessLoginEmptyAccountId() throws SQLException {
        ValueNode valueNode = mock(ValueNode.class);
        when(context.form("accountid")).thenReturn(valueNode);
        when(valueNode.valueOrNull()).thenReturn("");

        service.processLogin(context);

        verify(session).put(SESSION_ERROR_MESSAGE, "Account ID cannot be empty.");
        verify(context).sendRedirect(ROUTE_LOGIN);

        // Verify no database interaction
        verify(dataSource, never()).getConnection();

    }

    @Test
    void testProcessLoginNullValue() throws SQLException {
        ValueNode valueNode = mock(ValueNode.class);
        when(context.form("accountid")).thenReturn(valueNode);
        when(valueNode.valueOrNull()).thenReturn(null);

        service.processLogin(context);

        verify(session).put(SESSION_ERROR_MESSAGE, "Account ID cannot be empty.");
        verify(context).sendRedirect(ROUTE_LOGIN);

        verify(dataSource, never()).getConnection();
    }

    @Test
    void testProcessLoginSuccess() throws SQLException {
        String accountId = "account";
        String name = "Peter Tasker";
        BigDecimal balance = new BigDecimal(25);
        boolean roundUpEnabled = true;

        ValueNode valueNode = mock(ValueNode.class);
        when(context.form("accountid")).thenReturn(valueNode);
        when(valueNode.valueOrNull()).thenReturn(accountId);

        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("AccountID")).thenReturn(accountId);
        when(resultSet.getString("Name")).thenReturn(name);
        when(resultSet.getBigDecimal("Balance")).thenReturn(balance);
        when(resultSet.getBoolean("RoundUpEnabled")).thenReturn(roundUpEnabled);

        service.processLogin(context);

        verify(session).put("accountid", accountId);
        verify(session).put("name", name);

        verify(context).sendRedirect(ROUTE_ACCOUNT); // Adjust based on actual redirection

        verify(dataSource).getConnection();
        verify(preparedStatement).setString(1, accountId);
        verify(preparedStatement).executeQuery();
    }

    @Test
    void testProcessLoginDatabaseError() throws SQLException {
        ValueNode valueNode = mock(ValueNode.class);
        when(context.form("accountid")).thenReturn(valueNode);
        when(valueNode.valueOrNull()).thenReturn("myId");

        when(dataSource.getConnection()).thenThrow(new SQLException("Database error!!"));

        service.processLogin(context);

        verify(session).put(SESSION_ERROR_MESSAGE, "A database error occurred. Please try again.");
        verify(context).sendRedirect(ROUTE_LOGIN);

        verify(logger).error(contains("Database error"), anyString(), any(SQLException.class));
    }
}
