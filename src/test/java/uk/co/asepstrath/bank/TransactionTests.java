package uk.co.asepstrath.bank;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TransactionTests {

    @Test
    public void testGetters() {
        Transaction transaction = new Transaction("2023-04-10 08:43", 21, "8f95782c-7c83-4dd7-8856-0e19a0e0a075", "0043d8d9-846d-49cb-9b04-8d3823e9d8c9", "TOP", "PAYMENT");
            assertEquals("2023-04-10 08:43", transaction.getTimestamp());
            assertEquals(BigDecimal.valueOf(21), transaction.getAmount());
            assertEquals("8f95782c-7c83-4dd7-8856-0e19a0e0a075", transaction.getFrom());
            assertEquals("0043d8d9-846d-49cb-9b04-8d3823e9d8c9", transaction.getId());
            assertEquals("TOP", transaction.getTo());
            assertEquals("PAYMENT", transaction.getType());
    }
}
