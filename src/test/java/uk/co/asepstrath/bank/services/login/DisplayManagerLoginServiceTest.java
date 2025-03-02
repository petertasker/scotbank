package uk.co.asepstrath.bank.services.login;

import io.jooby.ModelAndView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.co.asepstrath.bank.Constants.*;

class DisplayManagerLoginServiceTest {

    @Mock
    private Logger logger;

    private DisplayManagerLoginService displayManagerLoginService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        displayManagerLoginService = new DisplayManagerLoginService(logger);
    }

    @Test
    void testDisplayLogin() {
        ModelAndView<Map<String, Object>> result = displayManagerLoginService.displayManagerLogin();
        assertNotNull(result);
        assertEquals(TEMPLATE_MANAGER_LOGIN, result.getView());
        assertNotNull(result.getModel());
    }
}
