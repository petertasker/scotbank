package uk.co.asepstrath.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jooby.Context;
import io.jooby.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ContextManager {
    private final Logger log = LoggerFactory.getLogger(ContextManager.class);

    // Do not put mutable variables in here, query them from the database
    public void addAccountDetailsToContext(Account account, Context ctx) throws JsonProcessingException {
        log.info("Adding account to context: {}", ctx);
        // Put the account instance into the session
        Session session = ctx.session();
        session.put("accountid", account.getAccountID());
        log.info("Account id added to context: {}", account.getAccountID());
        session.put("name", account.getName());
        log.info("Account name to context: {}", ctx);
    }
}