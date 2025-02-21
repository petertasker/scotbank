package uk.co.asepstrath.bank;

import io.jooby.ModelAndView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.controllers.LoginController;

import javax.sql.DataSource;
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
    private DataSource mockDataSource = mock(DataSource.class);
    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;
    @Mock
    private Logger mockLogger = mock(Logger.class);

    private static final String URL_PAGE_LOGIN = "login_user.hbs";


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
