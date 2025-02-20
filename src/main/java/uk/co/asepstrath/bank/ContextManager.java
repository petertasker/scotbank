package uk.co.asepstrath.bank;

import io.jooby.Context;
import io.jooby.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ContextManager {
    private final Logger log = LoggerFactory.getLogger(ContextManager.class);

    // Do not put mutable variables in here, query them from the database
    public void addAccountDetailsToContext(Account account, Context ctx) {
        log.info("Adding account to context: {}", ctx);
        // Put the account instance into the session
        Session session = ctx.session();
        session.put("accountid", account.getAccountID());
        log.info("Account id added to context: {}", account.getAccountID());
        session.put("name", account.getName());
        log.info("Account name to context: {}", ctx);
    }

    public String getAccountIdFromContext(Context ctx) {
        log.info("Getting account id from context: {}", ctx);
        Session session = ctx.session();
        String accountID = String.valueOf(session.get("accountid"));
        log.info("Found id from context: {}", ctx);
        return accountID;
    }

    public String getNameFromContext(Context ctx) {
        log.info("Getting name from context: {}", ctx);
        Session session = ctx.session();
        String name = String.valueOf(session.get("name"));
        log.info("Found name from context: {}", ctx);
        return name;
    }
}