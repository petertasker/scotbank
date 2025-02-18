package uk.co.asepstrath.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import io.jooby.annotation.GET;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import kong.unirest.core.UnirestException;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;

public class ContextManager {
    private static final Logger log = LoggerFactory.getLogger(ContextManager.class);
    private final ObjectMapper mapper = new ObjectMapper();


    // User can have no accounts, so either return nothing or the list or accounts
    public Optional<List<Account>> getAccountsFromContext(Context ctx) {
        try {
            Session session = ctx.session();
            String json = session.get("accounts").value();
            if (ObjectUtils.isEmpty(json)) {
                return Optional.of(new ArrayList<>());
            }
            // Get each value from the JSON accounts and parse it into the actual accounts
            List<Account> accounts = mapper.readValue(json, new TypeReference<List<Account>>() {});
            log.debug("Accounts: {}", json);
            return Optional.of(accounts);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public void addAccountToContext(Account account, Context ctx) throws JsonProcessingException {
        try {
            List<Account> accounts = getAccountsFromContext(ctx).orElse(new ArrayList<>());
            accounts.add(account);
            Session session = ctx.session();
            session.put("accounts", mapper.writeValueAsString(accounts));
        }
        catch (Exception e) {

        }
    }

    public Customer getCustomerFromContext(Context ctx) throws JsonProcessingException {
        try {
            // Grab customer details from the session
            Session session = ctx.session();
            String json = session.get("customer").value();
            if (ObjectUtils.isEmpty(json)) {
                throw new RuntimeException("Customer not found");
            }

            Customer customer = mapper.readValue(json, Customer.class);
            log.debug("Customer Retrieved from session: {}", customer);
            return customer;
        }
        catch (JsonProcessingException e) {
            log.debug("Failed to get customer context");
            throw e;
        }
    }

    public void addCustomerToContext(Customer customer, Context ctx) throws JsonProcessingException {
        try {
            Session session = ctx.session();
            String json = mapper.writeValueAsString(customer);
            log.debug("Customer added to session {}", json);
            // Add json string to the session
            session.put("customer", json);
        }
        catch (JsonProcessingException e) {
            log.debug("Failed to put customer into context");
            throw e;
        }
    }

}
