package uk.co.asepstrath.bank.services.manager;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.ValueNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.services.repository.ManagerRepository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static uk.co.asepstrath.bank.Constants.*;

class ViewManagerDashboardServiceTest {

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

    private ViewManagerDashboardService service;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);

        // Create real service but mock the repository
        service = spy(new ViewManagerDashboardService(dataSource, logger));

        when(dataSource.getConnection()).thenReturn(connection);
        when(context.session()).thenReturn(session);

        ValueNode managerIdValue = mock(ValueNode.class);
        ValueNode managerNameValue = mock(ValueNode.class);
        when(session.get(SESSION_MANAGER_ID)).thenReturn(managerIdValue);
        when(session.get(SESSION_MANAGER_NAME)).thenReturn(managerNameValue);

        when(managerIdValue.value()).thenReturn("M123");
        when(managerNameValue.value()).thenReturn("Test Manager");

    }

    @Test
    void renderDashboardWithAccounts() throws SQLException {
        // Arrange
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("A001", "John Smith", BigDecimal.valueOf(1000.50), false));
        accounts.add(new Account("A002", "Jane Doe", BigDecimal.valueOf(2500.75), false));

        // Mock the repository call
        ManagerRepository repository = mock(ManagerRepository.class);
        when(repository.getAllAccounts(any(Connection.class))).thenReturn(accounts);

        // Inject mocked repository using reflection
        try {
            java.lang.reflect.Field field = ViewManagerDashboardService.class.getDeclaredField("managerRepository");
            field.setAccessible(true);
            field.set(service, repository);
        }
        catch (Exception e) {
            fail("Failed to inject mocked repository: " + e.getMessage());
        }

        // Act
        ModelAndView<Map<String, Object>> result = service.renderDashboard(context);

        // Assert
        assertNotNull(result);
        assertEquals(TEMPLATE_MANAGER_DASHBOARD, result.getView());

        Map<String, Object> model = result.getModel();


        assertTrue((Boolean) model.get(ACCOUNT_OBJECT_LIST_EXISTS));

        List<Map<String, Object>> displayAccounts = (List<Map<String, Object>>) model.get(ACCOUNT_OBJECT_LIST);
        assertEquals(2, displayAccounts.size());

        Map<String, Object> firstAccount = displayAccounts.get(0);
        assertEquals("A001", firstAccount.get("accountid"));
        assertEquals("John Smith", firstAccount.get("name"));
        assertEquals("1,000.50", firstAccount.get("balance"));

        Map<String, Object> secondAccount = displayAccounts.get(1);
        assertEquals("A002", secondAccount.get("accountid"));
        assertEquals("Jane Doe", secondAccount.get("name"));
        assertEquals("2,500.75", secondAccount.get("balance"));
    }

    @Test
    void renderDashboardWithEmptyAccounts() throws SQLException {
        // Arrange
        List<Account> emptyAccounts = new ArrayList<>();

        // Mock the repository call
        ManagerRepository repository = mock(ManagerRepository.class);
        when(repository.getAllAccounts(any(Connection.class))).thenReturn(emptyAccounts);

        // Inject mocked repository using reflection
        try {
            java.lang.reflect.Field field = ViewManagerDashboardService.class.getDeclaredField("managerRepository");
            field.setAccessible(true);
            field.set(service, repository);
        }
        catch (Exception e) {
            fail("Failed to inject mocked repository: " + e.getMessage());
        }

        // Act
        ModelAndView<Map<String, Object>> result = service.renderDashboard(context);

        // Assert
        Map<String, Object> model = result.getModel();
        assertFalse((Boolean) model.get(ACCOUNT_OBJECT_LIST_EXISTS));

        List<Map<String, Object>> displayAccounts = (List<Map<String, Object>>) model.get(ACCOUNT_OBJECT_LIST);
        assertTrue(displayAccounts.isEmpty());
    }
}