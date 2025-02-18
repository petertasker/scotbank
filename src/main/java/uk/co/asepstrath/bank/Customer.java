package uk.co.asepstrath.bank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class Customer extends User {

    @JsonCreator
    public Customer(
            @JsonProperty("userName") String name,
            @JsonProperty("email") String email
            ) {
        super(name, email);
    }

    // Create new account for this customer
    public Account createAccount(BigDecimal balance) {
        return new Account(this, balance);
    }
}
