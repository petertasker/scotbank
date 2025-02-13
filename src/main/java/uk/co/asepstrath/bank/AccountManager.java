package uk.co.asepstrath.bank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {

    public static List<Account> generateExampleAccounts(){
        List<Customer> exampleAccounts = List.of(
                new Customer("Rachel"),
                new Customer("Monica"),
                new Customer("Pheobe"),
                new Customer("Joey"),
                new Customer("Ross"),
                new Customer("Chandler") // Added Chandler for completeness
        );

        List<Account> accounts = new ArrayList<>();

        // Create an account for each friend
        for (Customer account: exampleAccounts) {
            accounts.add(account.createAccount());
        }

        return accounts;
    }
}
