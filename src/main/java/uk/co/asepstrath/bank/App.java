package uk.co.asepstrath.bank;

import io.jooby.jackson.JacksonModule;
import io.jooby.netty.NettyServer;
import io.jooby.Jooby;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import org.slf4j.Logger;



import uk.co.asepstrath.bank.controllers.UserController;
import uk.co.asepstrath.bank.controllers.UserController_;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

        mvc(new UserController_(ds,log));
        /*
        Finally we register our application lifecycle methods
         */
        onStarted(() -> onStart());
        onStop(() -> onStop());
    }

    public static void main(final String[] args) {
        runApp(args, App::new);
    }

    /*
    This function will be called when the application starts up,
    it should be used to ensure that the DB is properly setup
     */
    public void onStart() {
        Logger log = getLog();
        log.info("Starting Up...");

        // Fetch DB Source
        DataSource ds = require(DataSource.class);
        // Open Connection to DB
        try (Connection connection = ds.getConnection()) {
            //
            Statement stmt = connection.createStatement();
            // Create Pseudo Database with users and accounts
            stmt.executeUpdate(
                    "CREATE TABLE `Users` (" +
                    "`userID` varchar(255) NOT NULL, " +
                    "`name` varchar(255) NOT NULL, " +
                    "email varchar(255) NOT NULL, " +
                    "`password` varchar(255) NOT NULL, " +
                    " PRIMARY KEY (`userID`))");

            stmt.executeUpdate(
                    "CREATE TABLE `Accounts` (" +
                            "`AccountID` varchar(255) NOT NULL," +
                            "`Balance` DECIMAL NOT NULL," +
                            "`roundUpEnabled` bit NOT NULL ," +
                            "`userID` varchar(255) NOT NULL," +
                            "PRIMARY KEY (`AccountID`)," +
                            "FOREIGN KEY (`userID`) REFERENCES `Users`(`userID`))");

            // Assume this comes from the register part of the system
            List<Customer> customers = AccountManager.generateExampleCustomers();
            List<Account> accounts = AccountManager.generateExampleAccounts();

            // Example generate new user
            for (Customer customer : customers) {
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Users VALUES (?, ?)");
                pstmt.setString(1, customer.getUserID());
                pstmt.setString(2, customer.getUserName());
            }

            // Example generate new account
            for (Account account : accounts) {
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Accounts VALUES (?, ?, ?, ?)");
                pstmt.setString(1, account.getAccountID());
                pstmt.setBigDecimal(2, account.getBalance());
                pstmt.setInt(3, 0);
                pstmt.setString(4, account.getCustomerID());
            }
        }
        catch (SQLException e) {
            log.error("Database Creation Error",e);
        }
    }

    /*
    This function will be called when the application shuts down
     */
    public void onStop() {
        System.out.println("Shutting Down...");
    }

}
