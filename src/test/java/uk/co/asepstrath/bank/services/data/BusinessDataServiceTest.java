package uk.co.asepstrath.bank.services.data;

import kong.unirest.core.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.asepstrath.bank.Business;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BusinessDataServiceTest {

    private BusinessDataService businessDataService;
    private UnirestWrapper unirestWrapper;

    @BeforeEach
    void setUp() {
        unirestWrapper = mock(UnirestWrapper.class);
        businessDataService = new BusinessDataService();
        businessDataService.setUnirestWrapper(unirestWrapper);
    }

    @Test
    void testFetchDataSuccess() throws IOException {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.isSuccess()).thenReturn(true);
        String csvData = "id,name,category,sanctioned\n" +
                "ALD,Aldi,Groceries,false\n" +
                "AMA,Amazon,Online Retailer,false\n" +
                "ARG,Argos,Online Retailer,false\n" +
                "BOO,Boom Battle Bar,Entertainment,false\n" +
                "BOT,Boots,Health & Beauty,false\n" +
                "BUR,Burger King,Eating Out,false\n" +
                "CAF,Cafe Nero,Coffee,false\n" +
                "CEX,CEX,Entertainment,true\n" +
                "CLA,Clarks,Clothing,false";
        when(mockResponse.getBody()).thenReturn(csvData);
        when(unirestWrapper.get(anyString())).thenReturn(mockResponse);
        List<Business> businesses = businessDataService.fetchData();

        // Assertions
        assertEquals(9, businesses.size());
        assertEquals("ALD", businesses.getFirst().getID());
        assertEquals("Aldi", businesses.getFirst().getName());
        assertEquals("Groceries", businesses.getFirst().getCategory());
        assertEquals(false, businesses.getFirst().isSanctioned());
    }

    @Test
    void testFetchDataFailure() {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.isSuccess()).thenReturn(false);
        when(unirestWrapper.get(anyString())).thenReturn(mockResponse);
        assertThrows(IOException.class, () -> businessDataService.fetchData());
    }
}
