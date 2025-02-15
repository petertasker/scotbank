package uk.co.asepstrath.bank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {

    public static List<Customer> generateExampleCustomers() {
        List<Customer> exampleCustomers = List.of(
                new Customer("Rachel", "rachel@msn.com"),
                new Customer("Monica", "monica@msn.com"),
                new Customer("Pheobe", "pheobe@msn.com"),
                new Customer("Joey", "joey@msn.com"),
                new Customer("Ross", "ross@msn.com"),
                new Customer("Chandler", "chandler@msn.com") // Added Chandler for completeness
        );
        return exampleCustomers;
    }


    public static List<Account> generateExampleAccounts() {
        List<Account> accounts = new ArrayList<>();
        List<Customer> customers = generateExampleCustomers();
        // Create an account for each friend
        for (Customer customer: customers) {
            accounts.add(customer.createAccount());
        }

        return accounts;
    }
}
