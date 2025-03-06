package uk.co.asepstrath.bank.services.error;

import io.jooby.ModelAndView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static uk.co.asepstrath.bank.Constants.*;

class ErrorServiceTest {

    @Mock
    private Logger mockLogger;

    private ErrorService errorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        errorService = new ErrorService(mockLogger);
        verify(mockLogger).info("ErrorService created");
    }

    @Test
    void testRenderForbiddenPage() {
        // Call the method under test
        ModelAndView<Map<String, Object>> result = errorService.renderForbiddenPage();

        // Verify the result
        assertNotNull(result);
        assertEquals(TEMPLATE_ERROR, result.getView());

        // Verify model contains expected values
        Map<String, Object> model = result.getModel();
        assertEquals("Access Denied", model.get(SESSION_SERVER_ERROR_TITLE));
        assertEquals("403", model.get(SESSION_SERVER_ERROR_CODE));
        assertEquals("You don't have permission to access this resource.", model.get(SESSION_SERVER_ERROR_MESSAGE));
        assertEquals("Please login with appropriate credentials.", model.get(SESSION_SERVER_ERROR_SUGGESTION));
    }

    @Test
    void testRenderNotFoundPage() {
        // Call the method under test
        ModelAndView<Map<String, Object>> result = errorService.renderNotFoundPage();

        // Verify logger was called
        verify(mockLogger).debug("Rendering 404 not found page");

        // Verify the result
        assertNotNull(result);
        assertEquals(TEMPLATE_ERROR, result.getView());

        // Verify model contains expected values
        Map<String, Object> model = result.getModel();
        assertEquals("Page Not Found", model.get(SESSION_SERVER_ERROR_TITLE));
        assertEquals("404", model.get(SESSION_SERVER_ERROR_CODE));
        assertEquals("The page you're looking for doesn't exist or has been moved.", model.get(SESSION_SERVER_ERROR_MESSAGE));
        assertEquals("Please check the URL or return to the homepage.", model.get(SESSION_SERVER_ERROR_SUGGESTION));

    }

    @Test
    void testRenderMethodNotAllowedPage() {
        // Call the method under test
        ModelAndView<Map<String, Object>> result = errorService.renderMethodNotAllowedPage();

        // Verify logger was called
        verify(mockLogger).debug("Rendering 405 method not allowed page");

        // Verify the result
        assertNotNull(result);
        assertEquals(TEMPLATE_ERROR, result.getView());

        // Verify model contains expected values
        Map<String, Object> model = result.getModel();
        assertEquals("Method Not Allowed", model.get(SESSION_SERVER_ERROR_TITLE));
        assertEquals("405", model.get(SESSION_SERVER_ERROR_CODE));
        assertEquals("The requested method is not supported for this resource.", model.get(SESSION_SERVER_ERROR_MESSAGE));
        assertEquals("Please use a different method or contact support if you believe this is an error.", model.get(SESSION_SERVER_ERROR_SUGGESTION));

    }

    @Test
    void testRenderGenericErrorPage() {
        // Call the method under test
        ModelAndView<Map<String, Object>> result = errorService.renderGenericErrorPage();

        // Verify logger was called
        verify(mockLogger).debug("Rendering generic error page");

        // Verify the result
        assertNotNull(result);
        assertEquals(TEMPLATE_ERROR, result.getView());

        // Verify model contains expected values
        Map<String, Object> model = result.getModel();
        assertEquals("Something Went Wrong", model.get(SESSION_SERVER_ERROR_TITLE));
        assertEquals("Error", model.get(SESSION_SERVER_ERROR_CODE));
        assertEquals("We encountered an unexpected issue while processing your request.", model.get(SESSION_SERVER_ERROR_MESSAGE));
        assertEquals("Please try again later or contact customer support if the problem persists.", model.get(SESSION_SERVER_ERROR_SUGGESTION));

    }
}