package uk.co.asepstrath.bank;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.Value;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import uk.co.asepstrath.bank.controllers.AccountController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Map;

import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AccountControllerTests {

    @Mock
    private AccountController accountController;
    @Mock
    private Context mockCtx;
    @Mock
    private Session mockSession;
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws Exception {
        mockCtx = mock(Context.class);
        mockSession = mock(Session.class);
        DataSource mockDataSource = mock(DataSource.class);
        Logger mockLogger = mock(Logger.class);
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        when(mockCtx.session()).thenReturn(mockSession);
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        accountController = new AccountController(mockDataSource, mockLogger);
    }

    @Test
    void testViewAccountWithTransactions() throws Exception {
        // Mock session values

        Value mockNameValue = mock(Value.class);
        when(mockNameValue.toString()).thenReturn("John Doe");
        Value mockAccountValue = mock(Value.class);
        when(mockAccountValue.toString()).thenReturn("12345");

        when(mockSession.get("name")).thenReturn(mockNameValue);
        when(mockSession.get("accountid")).thenReturn(mockAccountValue);

        // Mock result set with transactions
        when(mockResultSet.next()).thenReturn(true, false); // One transaction
        Timestamp mockTimestamp = new Timestamp(new DateTime(2024, 2, 21, 12, 0).getMillis());
        when(mockResultSet.getTimestamp("Timestamp")).thenReturn(mockTimestamp);
        when(mockResultSet.getInt("Amount")).thenReturn(100);
        when(mockResultSet.getString("SenderID")).thenReturn("12345");
        when(mockResultSet.getString("TransactionID")).thenReturn("TXN001");
        when(mockResultSet.getString("ReceiverID")).thenReturn("67890");
        when(mockResultSet.getString("TransactionType")).thenReturn("Transfer");

        // Execute method
        ModelAndView<Map<String, Object>> result = accountController.viewAccount(mockCtx);

        // Validate ModelAndView
        assertNotNull(result);
        Map<String, Object> model = result.getModel();
        assertEquals("John Doe", model.get("name").toString());
        assertEquals("12345", model.get("accountid").toString());

    }
}
