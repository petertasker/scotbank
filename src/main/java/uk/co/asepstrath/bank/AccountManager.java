package uk.co.asepstrath.bank;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {

    public static List<Account> exampleAccounts(){
        List<Account> accounts = new ArrayList<Account>();

        accounts.add(new Account("Rachel", BigDecimal.valueOf(50.00)));
        accounts.add(new Account("Monica", BigDecimal.valueOf(100.00)));
        accounts.add(new Account("Phoebe", BigDecimal.valueOf(76.00)));
        accounts.add(new Account("Joey", BigDecimal.valueOf(23.90)));
        accounts.add(new Account("Chandler", BigDecimal.valueOf(3.00)));
        accounts.add(new Account("Ross", BigDecimal.valueOf(54.32)));

        return accounts;
    }
}
