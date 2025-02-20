package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BusinessTest {

    @Test
    public void TestGetters_Business(){
        Business business = new Business("ALD","Aldi","Groceries",false);
        Assertions.assertEquals("ALD",business.getID());
        Assertions.assertEquals("Aldi",business.getName());
        Assertions.assertEquals("Groceries",business.getCategory());
        Assertions.assertFalse(business.isSanctioned());
    }
}
