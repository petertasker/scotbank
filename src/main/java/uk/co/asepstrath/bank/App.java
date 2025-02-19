package uk.co.asepstrath.bank;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.jooby.*;
import io.jooby.jackson.JacksonModule;
import io.jooby.netty.NettyServer;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

//import uk.co.asepstrath.bank.controllers.AccountController;
//import uk.co.asepstrath.bank.controllers.DashboardController;
//import uk.co.asepstrath.bank.controllers.CustomerController;
//import uk.co.asepstrath.bank.controllers.AccountController_;
//import uk.co.asepstrath.bank.controllers.DashboardController_;
//import uk.co.asepstrath.bank.controllers.CustomerController_;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
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
//        mvc(new CustomerController_(ds,log));
//        mvc(new DashboardController_(ds,log));
//        mvc(new AccountController_(ds,log));
        /*
        Finally we register our application lifecycle methods
         */
        onStarted(this::onStart);
        onStop(this::onStop);

        // Redirect to login page if no session exists
        before(ctx -> {
            String path = ctx.getRequestPath();
            if (!path.equals("/login")) {
                Session session = ctx.sessionOrNull();
                if (session == null) {
                    ctx.sendRedirect("/login");
                }
            }
        });
        // Dashboard as landing page
        get("/", ctx -> {
            ctx.sendRedirect("/dashboard");
            return ctx;
        });

    }

    public static void main(final String[] args) {
        runApp(args, App::new);
    }

    /*
    This function will be called when the application starts up,
     */
    public void onStart() {
        Logger log = getLog();
        log.info("Starting Up...");

        //Fetch DB Source
        DataSource ds = require(DataSource.class);
        // Open Connection to DB
        try (Connection connection = ds.getConnection()) {
            Statement stmt = connection.createStatement();
            // Create Pseudo Database with users and accounts
            stmt.executeUpdate(
                    "CREATE TABLE `Accounts` (" +
                            "`AccountID` varchar(255) NOT NULL, " +
                            "`Balance` DECIMAL NOT NULL, " +
                            "`Name` varchar(255) NOT NULL, " +
                            "`RoundUpEnabled` BIT NOT NULL, " +
                            "PRIMARY KEY (`AccountID`));"
            );
            stmt.close();
            // Assume this comes from the register part of the system
            URL url = new URL("https://api.asep-strath.co.uk/api/accounts");
            ObjectMapper mapper = new ObjectMapper();
            List<Account> accounts = mapper.readValue(url, new TypeReference<List<Account>>() {});
            for (Account account : accounts) {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Accounts VALUES (?, ?, ?, ?)");
                System.out.println("Inserting into database: ");
                System.out.println(account.getAccountID());
                preparedStatement.setString(1, account.getAccountID());
                System.out.println(account.getName());
                preparedStatement.setString(2, account.getName());
                System.out.println(account.getBalance());
                preparedStatement.setBigDecimal(3, account.getBalance());
                System.out.println(account.isRoundUpEnabled() ? "1" : "0");
//                preparedStatement.setString(4, account.isRoundUpEnabled() ? "1" : "0");
                preparedStatement.setBoolean(4, true);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getString("AccountID"));
                System.out.println(resultSet.getString("Balance"));
                System.out.println(resultSet.getString("RoundUpEnabled"));
                System.out.println(resultSet.getString("userID"));
            }
        }
        catch (SQLException e) {
            log.error("Database Creation Error",e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    This function will be called when the application shuts down
     */
    public void onStop() {
        System.out.println("Shutting Down...");
    }

}
