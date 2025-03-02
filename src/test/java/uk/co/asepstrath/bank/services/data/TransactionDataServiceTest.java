package uk.co.asepstrath.bank.services.data;

import kong.unirest.core.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.asepstrath.bank.Transaction;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionDataServiceTest {
    
    private TransactionDataService transactionDataService;
    private UnirestWrapper unirestWrapper;

    @BeforeEach
    void setUp() {
        unirestWrapper = mock(UnirestWrapper.class);
        transactionDataService = new TransactionDataService();
        transactionDataService.setUnirestWrapper(unirestWrapper);
    }

    @Test
    void testFetchDataSuccess() throws XMLStreamException{
        HttpResponse<String> initialResponse = mock(HttpResponse.class);
        HttpResponse<String> pageResponse = mock(HttpResponse.class);
        
        when(initialResponse.isSuccess()).thenReturn(true);
        when(pageResponse.isSuccess()).thenReturn(true);
        
        // XML representing page 0 out of 154 pages
        String xmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<pageResult>" +
                "  <hasNext>true</hasNext>" +
                "  <hasPrevious>false</hasPrevious>" +
                "  <page>0</page>" +
                "  <results xsi:type=\"transactionModel\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "    <timestamp>2023-04-10 08:43</timestamp>" +
                "    <amount>21.00</amount>" +
                "    <from>8f95782c-7c83-4dd7-8856-0e19a0e0a075</from>" +
                "    <id>0043d8d9-846d-49cb-9b04-8d3823e9d8c9</id>" +
                "    <to>TOP</to>" +
                "    <type>PAYMENT</type>" +
                "  </results>" +
                "  <size>1</size>" +
                "  <totalPages>154</totalPages>" +
                "</pageResult>";
        
        // For page 1, return an empty response to terminate the loop
        String emptyXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<pageResult>" +
                "  <hasNext>true</hasNext>" +
                "  <hasPrevious>true</hasPrevious>" +
                "  <page>1</page>" +
                "  <size>0</size>" +
                "  <totalPages>154</totalPages>" +
                "</pageResult>";
        
        when(initialResponse.getBody()).thenReturn(xmlData);
        when(pageResponse.getBody()).thenReturn(xmlData, emptyXmlData);
        
        // Configure mock wrapper to return the initial response, page 0 and page 1 responses
        when(unirestWrapper.get(anyString())).thenReturn(initialResponse, pageResponse, pageResponse);
        
        List<Transaction> transactions = transactionDataService.fetchData();
        
        // Verify results
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
    }
}
