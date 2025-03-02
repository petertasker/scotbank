package uk.co.asepstrath.bank.services.login;

import io.jooby.ModelAndView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static uk.co.asepstrath.bank.Constants.TEMPLATE_LOGIN;

class DisplayLoginServiceTest {

    @Mock
    private Logger logger;

    private DisplayLoginService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new DisplayLoginService(logger);
    }

    @Test
    void testDisplayLogin() {
        ModelAndView<Map<String, Object>> result = service.displayLogin();
        assertNotNull(result);
        assertEquals(TEMPLATE_LOGIN, result.getView());
        assertNotNull(result.getModel());

    }
}
