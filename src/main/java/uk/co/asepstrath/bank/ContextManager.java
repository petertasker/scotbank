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
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.Customer;
import uk.co.asepstrath.bank.User;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
public class ContextManager {

    public Account getAccountFromContext(Context ctx) {
        try {
            Session session = ctx.session();
            ObjectMapper mapper = new ObjectMapper();
            String accountJson = String.valueOf(session.get("account"));

            return mapper.readValue(accountJson, Account.class);
        }
        catch (Exception e) {
            return null;
        }
    }

    public Customer getCustomerFromContext(Context ctx) {
        try {
            // Grab User details from the session

            Session session = ctx.session();
            ObjectMapper mapper = new ObjectMapper();

            String userJson = session.get("customer").toString();
            System.out.printf("userJson: %s\n", userJson);
            return mapper.readValue(userJson, Customer.class);
        }
        catch (Exception e) {
            return null;
        }
    }

    public void putCustomerIntoContext(Customer customer, Context ctx) {
        try {
            // Create Session
            Session session = ctx.session();

            // Map the attributes of User to a JSON string
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(customer);
            System.out.println("json: " + json);
            // Add json string to the session
            session.put("customer", json);
        }
        catch (Exception e) {
            return;
        }
    }

}
