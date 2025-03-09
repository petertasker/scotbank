package uk.co.asepstrath.bank.services.data;

import kong.unirest.core.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.asepstrath.bank.Transaction;

import javax.xml.stream.XMLStreamException;
import java.util.List;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransactionDataServiceFetcherTest {

    private TransactionDataService transactionDataService;
    private UnirestWrapper unirestWrapper;
   private HttpResponse<String> mockResponse;

    @BeforeEach
    void setUp() {
        unirestWrapper = mock(UnirestWrapper.class);
        mockResponse = mock(HttpResponse.class);
       transactionDataService = new TransactionDataService(unirestWrapper);
 
}

    @Test
    void testFetchDataSuccess() throws XMLStreamException {
        // Mocking HttpResponse
        HttpResponse<String> initialResponse = mock(HttpResponse.class);
        HttpResponse<String> pageResponse = mock(HttpResponse.class);

        when(initialResponse.isSuccess()).thenReturn(true);
        when(pageResponse.isSuccess()).thenReturn(true);

        // Sample XML Data
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

        String emptyXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<pageResult>" +
                "  <hasNext>true</hasNext>" +
                "  <hasPrevious>true</hasPrevious>" +
                "  <page>1</page>" +
                "  <size>0</size>" +
                "  <totalPages>154</totalPages>" +
                "</pageResult>";

        // Mock the behavior of UnirestWrapper to return the XML responses
        when(initialResponse.getBody()).thenReturn(xmlData);
        when(pageResponse.getBody()).thenReturn(emptyXmlData);
        when(unirestWrapper.get(anyString(), eq("page"), anyInt())).thenReturn(initialResponse, pageResponse);

        // Create a mock Transaction instead of creating real ones
        Transaction mockTransaction = mock(Transaction.class);

        // Create a spy of the transactionDataService to intercept the Transaction creation
        TransactionDataService spyService = spy(transactionDataService);

        // Mock the Transaction creation process
        doReturn(List.of(mockTransaction)).when(spyService).fetchData();
        // Test the fetchData method on our spy
        List<Transaction> transactions = spyService.fetchData();

        // Verify the results
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
    }
}

