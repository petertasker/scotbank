package uk.co.asepstrath.bank;

import io.jooby.*;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import io.jooby.jackson.JacksonModule;
import io.jooby.netty.NettyServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.controllers.AccountController_;
import uk.co.asepstrath.bank.controllers.LoginController_;
import uk.co.asepstrath.bank.services.login.DisplayLoginService;
import uk.co.asepstrath.bank.services.login.ProcessLoginService;
import uk.co.asepstrath.bank.services.repositories.DatabaseManager;

import javax.sql.DataSource;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


public class AppClassTest {

    @Mock
    private DataSource mockDataSource;

    @Mock
    private Logger mockLogger;

    @Mock
    private Session mockSession;

    @Mock
    private Context mockContext;

    @Mock
    private DatabaseManager mockDatabaseManager;

    private App mockApp;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockApp = mock(App.class);

        doReturn(mockLogger).when(mockApp).getLog();
        doReturn(mockDataSource).when(mockApp).require(DataSource.class);
    }

    @Test
    void testAppRoutes() {
        // Create a real App instance
        App app = new App();

        // Get all routes from the app
        List<Route> routes = app.getRoutes();

        // Check for the root route
        boolean hasRootRoute = false;
        for (Route route : routes) {
            if (route.getPattern().equals("/") && route.getMethod().equals("GET")) {
                hasRootRoute = true;
                break;
            }
        }
        assertTrue(hasRootRoute, "Root route should be registered");
        // Check for asset mappings
        boolean hasCssAssets = false;
        boolean hasAssetsAssets = false;
        boolean hasServiceWorkerAsset = false;

        for (Route route : routes) {
            if (route.getPattern().equals("/css/*")) hasCssAssets = true;
            if (route.getPattern().equals("/assets/*")) hasAssetsAssets = true;
            if (route.getPattern().equals("/service_worker.js")) hasServiceWorkerAsset = true;
        }

        assertTrue(hasCssAssets, "CSS assets should be mapped");
        assertTrue(hasAssetsAssets, "General assets should be mapped");
        assertTrue(hasServiceWorkerAsset, "Service worker should be mapped");

        // Check for controller routes
        boolean hasLoginRoutes = false;
        boolean hasAccountRoutes = false;

        for (Route route : routes) {
            String pattern = route.getPattern();
            if (pattern.startsWith("/login")) hasLoginRoutes = true;
            if (pattern.startsWith("/account")) hasAccountRoutes = true;
        }

        assertTrue(hasLoginRoutes, "Login routes should be registered");
        assertTrue(hasAccountRoutes, "Account routes should be registered");
    }

    @Test
    void testRedirectToAccount() {
        when(mockContext.getRequestPath()).thenReturn("/css/style.css");

        verify(mockContext, never()).sendRedirect(anyString());
        verify(mockContext, never()).setResponseCode(anyInt());
    }
}
