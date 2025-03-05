package uk.co.asepstrath.bank;

import io.jooby.*;
import io.jooby.jackson.JacksonModule;
import io.jooby.netty.NettyServer;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.services.login.DisplayLoginService;
import uk.co.asepstrath.bank.services.login.ProcessLoginService;
import uk.co.asepstrath.bank.services.repository.DatabaseManager;

import uk.co.asepstrath.bank.controllers.LoginController_;
import uk.co.asepstrath.bank.controllers.AccountController_;
import uk.co.asepstrath.bank.controllers.LogoutController_;
import uk.co.asepstrath.bank.controllers.ManagerController_;
import uk.co.asepstrath.bank.controllers.ErrorController_;



import javax.sql.DataSource;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.sql.*;

import static uk.co.asepstrath.bank.Constants.*;

public class App extends Jooby {

    public App() {
        // Account page as landing page
        get("/", ctx -> ctx.sendRedirect(ROUTE_ACCOUNT));

        // Ensure user is logged in
        before(ctx -> {
            String path = ctx.getRequestPath();
            // add any JS/ CSS files here
            if (path.startsWith("/css")) return;

            Session session = ctx.sessionOrNull();
            boolean userLoggedIn = session != null && session.get("name").isPresent() && session.get("accountid").isPresent();
            if (!userLoggedIn && !path.startsWith(ROUTE_LOGIN)
                    && !path.equals("/manager/login") && !path.equals("/manager/login/process")){
                ctx.setResponseCode(401).sendRedirect("/login");
            }

        });

        // Unbelievable that I cannot switch on this
        error((ctx, cause, code) -> {
            if (code == StatusCode.FORBIDDEN) {
                ctx.setResponseCode(403).sendRedirect(ROUTE_ERROR + ROUTE_403_FORBIDDEN);
            }
            else if (code == StatusCode.NOT_FOUND) {
                ctx.setResponseCode(404).sendRedirect(ROUTE_ERROR + ROUTE_404_NOT_FOUND);
            }
            else if (code == StatusCode.METHOD_NOT_ALLOWED) {
                ctx.setResponseCode(405).sendRedirect(ROUTE_ERROR + ROUTE_405_METHOD_NOT_ALLOWED);
            }
            else {
                ctx.sendRedirect(ROUTE_ERROR + ROUTE_GENERIC_ERROR);
            }
        });

        /*
        This section is used for setting up the Jooby Framework modules
         */
        install(new NettyServer());
        install(new UniRestExtension());
        install(new HandlebarsModule());
        install(new HikariModule("mem"));
        install(new JacksonModule()); // Handle JSON requests
        /*
        This will host any files in src/main/resources/assets on <host>/assets
         */
        assets("/css/*", "/css");
        assets("/assets/*", "/assets");
        assets("/service_worker.js","/service_worker.js");

        /*
        Now we set up our controllers and their dependencies
         */
        DataSource ds = require(DataSource.class);
        Logger log = getLog();

        DisplayLoginService displayLoginService = new DisplayLoginService(log);
        ProcessLoginService processLoginService = new ProcessLoginService(ds, log);

        mvc(new AccountController_(ds, log));
        mvc(new LoginController_(displayLoginService, processLoginService, log));
        mvc(new ManagerController_(ds, log));
        mvc(new LogoutController_(log));
        mvc(new ErrorController_(log));


        /*
        Finally we register our application lifecycle methods
         */
        onStarted(this::onStart);
        onStop(this::onStop);
    }

    public static void main(final String[] args) {
        runApp(args, App::new);
    }

    /*
    This function will be called when the application starts up,
     */
    public void onStart() throws SQLException, XMLStreamException, IOException {
        Logger log = getLog();
        log.info("Starting Up...");

        //Fetch DB Source
        DataSource dataSource = require(DataSource.class);

        // Create Database and tables with initial data
        DatabaseManager databaseManager = new DatabaseManager(dataSource, log);
        databaseManager.initialise();
    }

    /*
    This function will be called when the application shuts down
     */
    public void onStop() {
        Logger log = getLog();
        log.info("Shutting Down...");
    }

}
