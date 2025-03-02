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
        String csvData = "id,name,category,sanctioned\nALD,Aldi,Groceries,false\nAMA,Amazon,Online Retailer,false\nARG,Argos,Online Retailer,false\nBOO,Boom Battle Bar,Entertainment,false\nBOT,Boots,Health & Beauty,false\nBUR,Burger King,Eating Out,false\nCAF,Cafe Nero,Coffee,false\nCEX,CEX,Entertainment,true\nCLA,Clarks,Clothing,false".formatted();
        when(mockResponse.getBody()).thenReturn(csvData);
        when(unirestWrapper.get(anyString())).thenReturn(mockResponse);
        List<Business> businesses = businessDataService.fetchData();

        // Assertions
        assertEquals(9, businesses.size());
        assertEquals("ALD", businesses.getFirst().getID());
        assertEquals("Aldi", businesses.getFirst().getName());
        assertEquals("Groceries", businesses.getFirst().getCategory());
        assertFalse(businesses.getFirst().isSanctioned());
    }

    @Test
    void testFetchDataFailure() {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.isSuccess()).thenReturn(false);
        when(unirestWrapper.get(anyString())).thenReturn(mockResponse);
        assertThrows(IOException.class, () -> businessDataService.fetchData());
    }
}
