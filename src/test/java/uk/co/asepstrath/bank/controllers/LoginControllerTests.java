package uk.co.asepstrath.bank.controllers;

import io.jooby.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import uk.co.asepstrath.bank.services.login.ServiceLoginDisplay;
import uk.co.asepstrath.bank.services.login.ServiceLoginProcess;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LoginControllerTests {

    @Mock
    private Context mockContext = mock(Context.class);
    @Mock
    private DataSource mockDataSource = mock(DataSource.class);
    @Mock
    private Connection mockConnection = mock(Connection.class);
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet = mock(ResultSet.class);
    @Mock
    private Logger mockLogger = mock(Logger.class);
    @Mock
    private Value mockValue = mock(Value.class);
    @Mock
    private Session mockSession = mock(Session.class);
    @Mock
    private ServiceLoginDisplay mockServiceLoginDisplay = mock(ServiceLoginDisplay.class);
    @Mock
    private ServiceLoginProcess mockServiceLoginProcess = mock(ServiceLoginProcess.class);
    @Mock
    private ModelAndView<Map<String, Object>> mockModelAndView;

    ControllerLogin controllerLogin = new ControllerLogin(mockServiceLoginDisplay, mockServiceLoginProcess, mockLogger);
    private ServiceLoginProcess serviceLoginProcess;

    @BeforeEach
    void setUp() throws SQLException {

        MockitoAnnotations.openMocks(this);

        // Set up mock chain for database operations
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Set up context and session
        when(mockContext.session()).thenReturn(mockSession);

        // Initialize controller with mocked dependencies
        controllerLogin = new ControllerLogin(mockServiceLoginDisplay, mockServiceLoginProcess, mockLogger);
        serviceLoginProcess = new ServiceLoginProcess(mockDataSource, mockLogger);
    }

    /*
     * Changing testing from testing the entire controller to just testing each class.
    */

    @Test
    void testDisplayLogin() {
        // Mock DisplayLogin
        when(mockServiceLoginDisplay.displayLogin()).thenReturn(mockModelAndView);
        // Act
        ModelAndView<Map<String, Object>> result = controllerLogin.displayLogin();

        assertNotNull(result);
        assertEquals(mockModelAndView, result);
        verify(mockServiceLoginDisplay).displayLogin();
    }

    // Test that the logincontroller was succesful in calling loginProcess
    @Test
    void testLoginProcessCall() {
        // Mock ProcessLogin
        when(mockServiceLoginProcess.processLogin(mockContext)).thenReturn(mockModelAndView);

        // Act
        ModelAndView<Map<String, Object>> result = controllerLogin.processLogin(mockContext);
        // Assert
        assertNotNull(result);
        assertEquals(mockModelAndView, result);
        
        verify(mockServiceLoginProcess).processLogin(mockContext);
        // Verify no other interactions 
        verifyNoMoreInteractions(mockServiceLoginProcess);
        // Verify no unexpected interactions with the session
        verifyNoMoreInteractions(mockSession);        
    }

    @Test
    void testLoginProcessSuccess() throws SQLException {
        String accountId = "12";
        String name = "Peter Tasker";
        BigDecimal balance = new BigDecimal("1000.00");
        boolean roundUpEnabled = true;

        // Mock form values
        ValueNode mockAccountIdNode = mock(ValueNode.class);
        when(mockAccountIdNode.valueOrNull()).thenReturn(accountId);
        when(mockContext.form("accountid")).thenReturn(mockAccountIdNode);

        // Mock database response
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("AccountID")).thenReturn(accountId);
        when(mockResultSet.getString("Name")).thenReturn(name);
        when(mockResultSet.getBigDecimal("Balance")).thenReturn(balance);
        when(mockResultSet.getBoolean("RoundUpEnabled")).thenReturn(roundUpEnabled);

        // Act
        ModelAndView<Map<String, Object>> result = serviceLoginProcess.processLogin(mockContext);

        // Assert
        assertNull(result); // Should be null because of redirect

        verify(mockSession).put("accountid", accountId);
        verify(mockSession).put("name", name);

        // Verify no other interactions with session.put()
        verify(mockContext).sendRedirect("/account");
        verify(mockSession, times(2)).put(anyString(), (String) any());
    }
}
