package uk.co.asepstrath.bank;

import io.jooby.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.controllers.LoginController;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
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

    private static final String URL_PAGE_LOGIN = "login_user.hbs";


    LoginController loginController = new LoginController(mockDataSource, mockLogger);


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
        loginController = new LoginController(mockDataSource, mockLogger);


    }

    @Test
    void testDisplayLogin() {
        ModelAndView<Map<String, Object>> result = loginController.displayLogin();

        assertNotNull(result);
        assertEquals(URL_PAGE_LOGIN, result.getView());
        assertNotNull(result.getModel());
        assertTrue(result.getModel().isEmpty());
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
        ModelAndView<Map<String, Object>> result = loginController.processLogin(mockContext);

        // Assert
        verify(mockContext).sendRedirect("/account");
        assertNull(result); // Should be null because of redirect

        // Verify only the session attributes that ContextManager actually sets
        verify(mockSession).put("accountid", accountId);
        verify(mockSession).put("name", name);

        // Verify no other interactions with session.put()
        verify(mockSession, times(2)).put(anyString(), (String) any());
    }

}
