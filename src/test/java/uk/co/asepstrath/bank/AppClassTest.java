package uk.co.asepstrath.bank;

import io.jooby.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


class AppClassTest {

    @Mock
    private DataSource mockDataSource;

    @Mock
    private Logger mockLogger;

    @Mock
    private Context mockContext;


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
