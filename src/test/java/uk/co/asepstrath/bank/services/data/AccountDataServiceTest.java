package uk.co.asepstrath.bank.services.data;

import com.fasterxml.jackson.core.JsonParseException;
import kong.unirest.core.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.services.data.AccountDataService;
import uk.co.asepstrath.bank.services.data.UnirestWrapper;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountDataServiceTest {

    private AccountDataService accountDataService;
    private UnirestWrapper unirestWrapper;

    @BeforeEach
    void setUp() {
        unirestWrapper = mock(UnirestWrapper.class);
        accountDataService = new AccountDataService();
        // Inject the mocked UnirestWrapper into AccountDataService
        accountDataService.setUnirestWrapper(unirestWrapper);
    }

    @Test
    void testFetchDataSuccess() throws IOException {
        // Create mock response for successful API call
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.isSuccess()).thenReturn(true);

        // JSON structure that matches your Account class JsonProperty annotations
        when(mockResponse.getBody()).thenReturn("[{\"id\":\"1\", \"name\":\"Account1\", \"startingBalance\":100.0, \"roundUpEnabled\":true}]");

        // Configure mock wrapper to return the mock response
        when(unirestWrapper.get(anyString())).thenReturn(mockResponse);

        // Call the method under test
        List<Account> accounts = accountDataService.fetchData();

        // Assertions
        assertNotNull(accounts);
        assertEquals(1, accounts.size());
        assertEquals("1", accounts.getFirst().getAccountID());
        assertEquals("Account1", accounts.getFirst().getName());
    }

    @Test
    void testFetchDataFailure() {
        // Create mock response for failed API call
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.isSuccess()).thenReturn(false);
        when(mockResponse.getStatus()).thenReturn(404);

        // Configure mock wrapper to return the mock response
        when(unirestWrapper.get(anyString())).thenReturn(mockResponse);

        // Asserting that a JsonParseException is thrown
        assertThrows(JsonParseException.class, () -> accountDataService.fetchData());
    }
}