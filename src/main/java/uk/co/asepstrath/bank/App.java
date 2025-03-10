package uk.co.asepstrath.bank;

import io.jooby.Jooby;
import io.jooby.Session;
import io.jooby.StatusCode;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import io.jooby.jackson.JacksonModule;
import io.jooby.netty.NettyServer;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.controllers.*;
import uk.co.asepstrath.bank.services.login.DisplayLoginService;
import uk.co.asepstrath.bank.services.login.ProcessLoginService;
import uk.co.asepstrath.bank.services.repository.DatabaseManager;

import javax.sql.DataSource;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.sql.SQLException;

import static uk.co.asepstrath.bank.Constants.*;

public class App extends Jooby {

    public App() {
        // Account page as landing page
        get("/", ctx -> {
            // Check if manager is logged in
            Session session = ctx.sessionOrNull();
            boolean managerLoggedIn = session != null && session.get(SESSION_MANAGER_NAME).isPresent() && session.get(
                    SESSION_MANAGER_ID).isPresent();

            // Redirect to manager dashboard if logged in
            if (managerLoggedIn) {
                ctx.sendRedirect(ROUTE_MANAGER + ROUTE_DASHBOARD);
            } else {
                ctx.sendRedirect(ROUTE_ACCOUNT); // Regular user account page if manager is not logged in
            }
            return null;
        });
        // Ensure user is logged in
        before(ctx -> {
            String path = ctx.getRequestPath();
            // add any JS/ CSS files here
            if (path.startsWith("/css")) {
                return;
            }
            if (path.endsWith("/js")) {
                return;
            }
            if (path.endsWith(".ico")) {
                return;
            }

            Session session = ctx.sessionOrNull();
            boolean userLoggedIn = session != null && session.get(SESSION_ACCOUNT_NAME).isPresent() && session.get(
                    SESSION_ACCOUNT_ID).isPresent();
            boolean managerLoggedIn = session != null && session.get(SESSION_MANAGER_NAME).isPresent() && session.get(
                    SESSION_MANAGER_ID).isPresent();

            if (!userLoggedIn && !managerLoggedIn && !path.startsWith(ROUTE_LOGIN)
                    && !path.startsWith("/manager/login")) {
                ctx.setResponseCode(401).sendRedirect("/login");
            }


        });

        // Unbelievable that I cannot switch on this
        error((ctx, cause, code) -> {
            getLog().error("Error occurred: {}, Status code: {}", cause.getMessage(), code, cause);
            if (code == StatusCode.FORBIDDEN) {
                ctx.setResponseCode(403).sendRedirect(ROUTE_ERROR + ROUTE_403_FORBIDDEN);
            }
            else if (code == StatusCode.NOT_FOUND) {
                ctx.setResponseCode(404).sendRedirect(ROUTE_ERROR + ROUTE_404_NOT_FOUND);
            }
            else if (code == StatusCode.METHOD_NOT_ALLOWED) {
                ctx.setResponseCode(405).sendRedirect(ROUTE_ERROR + ROUTE_405_METHOD_NOT_ALLOWED);
            }
            else if (code == StatusCode.SERVER_ERROR) {
                ctx.setResponseCode(505).sendRedirect(ROUTE_ERROR + ROUTE_505_SERVER_ERROR);
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
        assets("/service_worker.js", "/service_worker.js");
        assets("/transaction-chart.js", "/transaction-chart.js");
        assets("/favicon.svg", "images/favicon.svg");
        get("favicon.ico", ctx -> ctx.sendRedirect("/favicon.svg"));
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
