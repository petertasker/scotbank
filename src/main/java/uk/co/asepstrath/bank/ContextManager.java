package uk.co.asepstrath.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
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


import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
public class ContextManager {

    public Account getAccountFromContext(Context ctx) {
        try {
            Session session = ctx.session();
            ObjectMapper mapper = new ObjectMapper();
            String json = String.valueOf(session.get("account"));

            return mapper.readValue(json, Account.class);
        }
        catch (Exception e) {
            return null;
        }
    }

    public void putAccountIntoContext(Account account, Context ctx) throws JsonProcessingException {
        try {
            Session session = ctx.session();
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(account);
            System.out.printf("Account JSON: " + json);
            session.put("account", json);
        }
        catch (JsonProcessingException e) {
            System.out.println("Failed to put account into context");
            throw e;
        }
    }


    public Customer getCustomerFromContext(Context ctx) throws JsonProcessingException {
        try {
            // Grab User details from the session

            Session session = ctx.session();
            ObjectMapper mapper = new ObjectMapper();

            String json = session.get("customer").toString();
            System.out.printf("Getting customer from context: %s\n", json);
            return mapper.readValue(json, Customer.class);
        }
        catch (JsonProcessingException e) {
            System.out.println("Failed to get customer context");
            throw e;
        }
    }

    public void putCustomerIntoContext(Customer customer, Context ctx) throws JsonProcessingException {
        try {
            // Create Session
            Session session = ctx.session();

            // Map the attributes of User to a JSON string
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(customer);
            System.out.println("Customer JSON: " + json);
            // Add json string to the session
            session.put("customer", json);
        }
        catch (JsonProcessingException e) {
            System.out.println("Failed to put customer into context");
            throw e;
        }
    }

}
