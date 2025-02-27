package uk.co.asepstrath.bank.services;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.joda.time.DateTime;
import uk.co.asepstrath.bank.Transaction;

import java.math.BigDecimal;

class TransactionTests {

    @Test
    void testGetters() {
        Transaction transaction = new Transaction(new DateTime(2025, 2, 14, 8, 30, 0), BigDecimal.valueOf(21), "8f95782c-7c83-4dd7-8856-0e19a0e0a075", "0043d8d9-846d-49cb-9b04-8d3823e9d8c9", "TOP", "PAYMENT",true);
        assertEquals(new DateTime(2025, 2, 14, 8, 30, 0), transaction.getTimestamp());
        assertEquals(BigDecimal.valueOf(21), transaction.getAmount());
        assertEquals("8f95782c-7c83-4dd7-8856-0e19a0e0a075", transaction.getFrom());
        assertEquals("0043d8d9-846d-49cb-9b04-8d3823e9d8c9", transaction.getId());
        assertEquals("TOP", transaction.getTo());
        assertEquals("PAYMENT", transaction.getType());
        assertTrue(transaction.getStatus());
    }
}
