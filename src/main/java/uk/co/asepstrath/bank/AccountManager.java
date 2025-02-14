package uk.co.asepstrath.bank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {

    public static List<Customer> generateExampleCustomers() {
        List<Customer> exampleCustomers = List.of(
                new Customer("Rachel"),
                new Customer("Monica"),
                new Customer("Pheobe"),
                new Customer("Joey"),
                new Customer("Ross"),
                new Customer("Chandler") // Added Chandler for completeness
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
