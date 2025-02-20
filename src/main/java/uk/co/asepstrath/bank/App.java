package uk.co.asepstrath.bank;

import io.jooby.*;
import io.jooby.jackson.JacksonModule;
import io.jooby.netty.NettyServer;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import org.slf4j.Logger;

//import uk.co.asepstrath.bank.controllers.AccountController;
//import uk.co.asepstrath.bank.controllers.DashboardController;
//import uk.co.asepstrath.bank.controllers.CustomerController;
//import uk.co.asepstrath.bank.controllers.AccountController_;
//import uk.co.asepstrath.bank.controllers.DashboardController_;
//import uk.co.asepstrath.bank.controllers.CustomerController_;

import javax.sql.DataSource;
import java.sql.*;

public class App extends Jooby {

    {
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
        assets("/assets/*", "/assets");
        assets("/service_worker.js","/service_worker.js");

        /*
        Now we set up our controllers and their dependencies
         */
        DataSource ds = require(DataSource.class);
        Logger log = getLog();
//        mvc(new CustomerController_(ds,log));
//        mvc(new DashboardController_(ds,log));
//        mvc(new AccountController_(ds,log));
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
    public void onStart() throws SQLException {
        Logger log = getLog();
        log.info("Starting Up...");

        //Fetch DB Source
        DataSource dataSource = require(DataSource.class);

        // Create Database and tables with initial data
        DatabaseInitialiser initialiser = new DatabaseInitialiser(dataSource);
        initialiser.initialise();
    }

    /*
    This function will be called when the application shuts down
     */
    public void onStop() {
        System.out.println("Shutting Down...");
    }

}
