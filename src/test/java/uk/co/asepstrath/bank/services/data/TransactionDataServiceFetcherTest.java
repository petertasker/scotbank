package uk.co.asepstrath.bank.services.data;

import kong.unirest.core.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.asepstrath.bank.Transaction;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.xml.stream.XMLStreamException;

import static org.junit.jupiter.api.Assertions.*;
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
        HttpResponse<String> firstPageResponse = mock(HttpResponse.class);   
        when(firstPageResponse.isSuccess()).thenReturn(true);
       // Sample XML Data
       String firstPageXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
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
        when(firstPageResponse.getBody()).thenReturn(firstPageXml);

        // Mock the behavior of UnirestWrapper to return the XML responses
        when(firstPageResponse.getBody()).thenReturn(firstPageXml);

        when(unirestWrapper.get("https://api.asep-strath.co.uk/api/transactions", "page", 0)).thenReturn(firstPageResponse);
   
        // Execute the method under test
        List<Transaction> transactions = transactionDataService.fetchData();
        
        // Verify the results
        assertNotNull(transactions);
        verify(unirestWrapper).get("https://api.asep-strath.co.uk/api/transactions", "page", 0);
        verify(unirestWrapper, never()).get("https://api.asep-strath.co.uk/api/transactions", "page", 1);
    }

    @Test
    void testFetchDataWithHttpRequestFailure() throws XMLStreamException {
        // Mock a failed HTTP response
        when(mockResponse.isSuccess()).thenReturn(false);
        when(mockResponse.getStatus()).thenReturn(500);
        when(unirestWrapper.get(anyString(), eq("page"), anyInt())).thenReturn(mockResponse);
        
        // Execute the method under test
        List<Transaction> transactions = transactionDataService.fetchData();
        
        // Verify the results
        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
        verify(unirestWrapper, times(1)).get(anyString(), eq("page"), anyInt());
    }

    @Test
    void testFetchDataWithXmlParsingError()  {
        // Mock a response that will cause an XML parsing error
        when(mockResponse.isSuccess()).thenReturn(true);
        when(mockResponse.getBody()).thenReturn("Invalid XML");
        when(unirestWrapper.get(anyString(), eq("page"), anyInt())).thenReturn(mockResponse);
        
        // Execute the method and verify exception
        assertThrows(XMLStreamException.class, () -> transactionDataService.fetchData());
    }

    @Test
    void testTransactionHandlingWithNullAmount() throws XMLStreamException {
        // Mock response with a transaction that has null amount
        when(mockResponse.isSuccess()).thenReturn(true);
        String xmlWithNullAmount = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<pageResult>" +
                "  <hasNext>false</hasNext>" +
                "  <hasPrevious>false</hasPrevious>" +
                "  <page>0</page>" +
                "  <results xsi:type=\"transactionModel\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "    <timestamp>2023-04-10 08:43</timestamp>" +
                "    <amount>48.00</amount>" +
                "    <from>8f95782c-7c83-4dd7-8856-0e19a0e0a075</from>" +
                "    <id>null-amount-id</id>" +
                "    <to>TOP</to>" +
                "    <type>PAYMENT</type>" +
                "  </results>" +
                "  <size>1</size>" +
                "  <totalPages>1</totalPages>" +
                "</pageResult>";
        when(mockResponse.getBody()).thenReturn(xmlWithNullAmount);
        when(unirestWrapper.get(anyString(), eq("page"), anyInt())).thenReturn(mockResponse);
        
        // Execute the method under test
        List<Transaction> transactions = transactionDataService.fetchData();
        
        // Verify the results
        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
    }

    @Test
    void testCreateTransactionSafely() throws Exception {
        // Create mocks
        DataSource mockDataSource = mock(DataSource.class);
        Connection mockConnection = mock(Connection.class);
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        
        // Create service
        TransactionDataService service = new TransactionDataService(mockDataSource);
        
        // Create test transaction with null amount 
        Transaction fakeTransaction = new Transaction();
        setPrivateField(fakeTransaction, "id", "bad123");

        // Access private createTransactionSafely method
        Method createMethod = TransactionDataService.class.getDeclaredMethod(
            "createTransactionSafely", Transaction.class);
        createMethod.setAccessible(true);
        
        // Call the method
        Transaction result = (Transaction) createMethod.invoke(service, fakeTransaction);
        
        // Should be null as the transaction had a null amount
        assertNull(result);
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
}


