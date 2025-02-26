package uk.co.asepstrath.bank.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.co.asepstrath.bank.Business;

class BusinessClassTests {

    @Test
    void testGetters(){
        Business business = new Business("ALD","Aldi","Groceries",false);
        Assertions.assertEquals("ALD",business.getID());
        Assertions.assertEquals("Aldi",business.getName());
        Assertions.assertEquals("Groceries",business.getCategory());
        Assertions.assertFalse(business.isSanctioned());
    }
}
