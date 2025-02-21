package uk.co.asepstrath.bank;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Value;
import io.jooby.ValueNode;
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

public class LoginControllerTests {

    @Mock
    private Context mockCtx;
    @Mock
    private DataSource mockDataSource = mock(DataSource.class);
    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;
    @Mock
    private Value mockFormValue;
    @Mock
    private Logger mockLogger = mock(Logger.class);
    @Mock
    private ContextManager mockContextManager;

    private static final String URL_PAGE_LOGIN = "login_user.hbs";
    private static final String URL_PAGE_ACCOUNT = "account.hbs";
    private static final String URL_ERROR = "error";
    private static final String URL_ACCOUNT_ID = "accountid";
    private static final String URL_ACCOUNT_NAME = "name";
    private static final String URL_TRANSACTION_OBJECTS = "transactions";
    private static final String URL_TRANSACTION_HAS_OBJECTS = "hastransactions";

    LoginController loginController = new LoginController(mockDataSource, mockLogger);


    @BeforeEach
    void setUp() throws SQLException {


        MockitoAnnotations.openMocks(this);

        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);


    }

    @Test
    void testDisplayLogin() {
        ModelAndView<Map<String, Object>> result = loginController.displayLogin();

        assertNotNull(result);
        assertEquals(URL_PAGE_LOGIN, result.getView());
        assertNotNull(result.getModel());
        assertTrue(result.getModel().isEmpty());
    }

}
