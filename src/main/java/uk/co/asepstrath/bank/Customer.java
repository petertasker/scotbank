package uk.co.asepstrath.bank;

import java.math.BigDecimal;

public class Customer extends User {

    public Customer(String name) {
        super(name);
    }

    // Create new account for this customer
    public Account createAccount() {
        return new Account(this, BigDecimal.ZERO);
    }
}
