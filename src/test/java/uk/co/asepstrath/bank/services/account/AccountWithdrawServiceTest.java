package uk.co.asepstrath.bank.services.account;
import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.services.repository.AccountRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static uk.co.asepstrath.bank.Constants.URL_PAGE_ACCOUNT_WITHDRAW;


class AccountWithdrawServiceTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Logger logger;

    @Mock
    private Context context;

    @Mock
    private Session session;

    @Mock
    private Connection connection;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private AccountWithdrawService service;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        Session session = Mockito.mock(Session.class);

        // Mock internal repository access with reflection to use our mock repository
        try {
            java.lang.reflect.Field field = AccountWithdrawService.class.getDeclaredField("accountRepository");
            field.setAccessible(true);
            field.set(service, accountRepository);
        } catch (Exception e) {
            fail("Failed to set mock accountRepository: " + e.getMessage());
        }
        service = new AccountWithdrawService(dataSource, logger);
        when(dataSource.getConnection()).thenReturn(connection);
        when(context.session()).thenReturn(session);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(context.session()).thenReturn(session);
    }

    @Test
    void testRenderWithdraw() {
        ModelAndView<Map<String, Object>> result = service.renderWithdraw(context);
        assertNotNull(result);
        assertEquals(URL_PAGE_ACCOUNT_WITHDRAW, result.getView());
    }
}