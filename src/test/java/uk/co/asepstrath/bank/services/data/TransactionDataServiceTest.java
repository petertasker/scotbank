package uk.co.asepstrath.bank.services.data;
//
//import kong.unirest.core.HttpResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import uk.co.asepstrath.bank.Transaction;
//
//
//import java.math.BigDecimal;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//
//import javax.sql.DataSource;
//import javax.xml.stream.XMLStreamException;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//public class TransactionDataServiceTest {
//
//    private TransactionDataService transactionDataService;
//    private UnirestWrapper unirestWrapper;
//
//    @BeforeEach
//    void setUp() {
//        unirestWrapper = mock(UnirestWrapper.class);
//        transactionDataService = new TransactionDataService();
//        transactionDataService.setUnirestWrapper(unirestWrapper);
//    }
//
//    @Test
//    void testFetchDataSuccess() throws XMLStreamException, SQLException {
//        // Mocking HttpResponse
//        HttpResponse<String> initialResponse = mock(HttpResponse.class);
//        HttpResponse<String> pageResponse = mock(HttpResponse.class);
//
//        when(initialResponse.isSuccess()).thenReturn(true);
//        when(pageResponse.isSuccess()).thenReturn(true);
//
//        // Sample XML Data
//        String xmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//                "<pageResult>" +
//                "  <hasNext>true</hasNext>" +
//                "  <hasPrevious>false</hasPrevious>" +
//                "  <page>0</page>" +
//                "  <results xsi:type=\"transactionModel\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
//                "    <timestamp>2023-04-10 08:43</timestamp>" +
//                "    <amount>21.00</amount>" +
//                "    <from>8f95782c-7c83-4dd7-8856-0e19a0e0a075</from>" +
//                "    <id>0043d8d9-846d-49cb-9b04-8d3823e9d8c9</id>" +
//                "    <to>TOP</to>" +
//                "    <type>PAYMENT</type>" +
//                "  </results>" +
//                "  <size>1</size>" +
//                "  <totalPages>154</totalPages>" +
//                "</pageResult>";
//
//        String emptyXmlData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//                "<pageResult>" +
//                "  <hasNext>true</hasNext>" +
//                "  <hasPrevious>true</hasPrevious>" +
//                "  <page>1</page>" +
//                "  <size>0</size>" +
//                "  <totalPages>154</totalPages>" +
//                "</pageResult>";
//
//        // Mock DataSource and Connection
//        DataSource mockDataSource = mock(DataSource.class);
//        Connection mockConnection = mock(Connection.class);
//        PreparedStatement mockStatement = mock(PreparedStatement.class);
//        ResultSet mockResultSet = mock(ResultSet.class);
//
//        // Ensure that prepareStatement returns the mock PreparedStatement
//        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
//        when(mockDataSource.getConnection()).thenReturn(mockConnection);
//        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
//        when(mockResultSet.next()).thenReturn(true, false);
//
//        // Mock account balance retrieval for the account
//        BigDecimal initialBalance = BigDecimal.valueOf(100.00); // Mocking an account balance
//        when(mockResultSet.getBigDecimal("balance")).thenReturn(initialBalance);
//
//        // Mock transaction data
//        when(mockResultSet.getString("timestamp")).thenReturn("2023-04-10 08:43");
//        when(mockResultSet.getBigDecimal("amount")).thenReturn(BigDecimal.valueOf(21.00));
//        when(mockResultSet.getString("from")).thenReturn("8f95782c-7c83-4dd7-8856-0e19a0e0a075");
//        when(mockResultSet.getString("id")).thenReturn("0043d8d9-846d-49cb-9b04-8d3823e9d8c9");
//        when(mockResultSet.getString("to")).thenReturn("TOP");
//        when(mockResultSet.getString("type")).thenReturn("PAYMENT");
//
//        // Mocking transactionDataService and its methods
//        TransactionDataService transactionDataService = new TransactionDataService(mockDataSource);
//
//        // Mock the behavior of UnirestWrapper to return the XML responses
//        when(initialResponse.getBody()).thenReturn(xmlData);
//        when(pageResponse.getBody()).thenReturn(xmlData, emptyXmlData);
//        when(unirestWrapper.get(anyString(), eq("page"), anyInt())).thenReturn(initialResponse, pageResponse);
//
//        // Test the fetchData method
//        List<Transaction> transactions = transactionDataService.fetchData();
//
//        // Verify the results
//        assertNotNull(transactions);
//        assertEquals(2, transactions.size());
//
//        // Verify that the connection was used correctly
//        verify(mockConnection, times(1)).prepareStatement(anyString());  // Verifying the usage of prepareStatement
//    }
//
//}
