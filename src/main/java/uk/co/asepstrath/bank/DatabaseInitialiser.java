package uk.co.asepstrath.bank;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.print.Doc;
import javax.sql.DataSource;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.xml.parsers.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.co.asepstrath.bank.parsers.XmlParser;

public class DatabaseInitialiser {

    private static final String SQL_CREATE_ACCOUNT = """
        CREATE TABLE Accounts (
            AccountID VARCHAR(255) NOT NULL,
            Balance DECIMAL(10,2) NOT NULL,
            Name VARCHAR(255) NOT NULL,
            RoundUpEnabled BIT NOT NULL,
            PRIMARY KEY (AccountID)
        )
    """;

    private static final String SQL_CREATE_BUSINESS = """
        CREATE TABLE Business (
            BusinessID VARCHAR(255) NOT NULL,
            Business_Name VARCHAR(255) NOT NULL,
            Category VARCHAR(255) NOT NULL,
            Sanctioned BIT NOT NULL,
            PRIMARY KEY (BusinessID)
        )
    """;

    private static final String SQL_CREATE_TRANSACTION = """
        CREATE TABLE Transactions (
            Timestamp DATETIME NOT NULL,
            Amount DECIMAL(10,2) NOT NULL,
            Sender VARCHAR(255) NOT NULL,
            Id VARCHAR(255) NOT NULL,
            Receiver VARCHAR(255) NOT NULL,
            TransactionType VARCHAR(255) NOT NULL,
            PRIMARY KEY (Id),
            FOREIGN KEY (Receiver) REFERENCES Business(BusinessID)
        )
    """;


    private final DataSource dataSource;
    Logger log = LoggerFactory.getLogger(DatabaseInitialiser.class);
    private final ObjectMapper mapper;
    private final DatabaseHandler dbHandler;

    public DatabaseInitialiser(DataSource dataSource) {
        this.dataSource = dataSource;
        this.mapper = new ObjectMapper();

        // Handle database operations
        dbHandler = new DatabaseHandler(dataSource);
    }

    public void initialise() throws SQLException {
        try {
            Connection connection = dataSource.getConnection();
            createAccountTable(connection);
            createBusinessTable(connection);
            createTransactionTable(connection);

            List<Account> accounts = fetchAccounts();
            for (Account account : accounts) {
                dbHandler.insertAccount(connection, account);
            }

            List<Business> businesses = fetchBusinesses();
            for(Business business : businesses) {
                dbHandler.insertBusiness(connection, business);
            }

            List<Transaction> transactions = fetchTransactions();
            for (Transaction transaction : transactions) {
                dbHandler.insertTransaction(connection, transaction);
            }



            connection.close();
        }
            catch (SQLException e) {
                throw new SQLException(e);
            }
    }

    private void createAccountTable(Connection connection) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(SQL_CREATE_ACCOUNT);

        }
        catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    private void createBusinessTable(Connection connection) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(SQL_CREATE_BUSINESS);

        }
        catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    private void createTransactionTable(Connection connection) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(SQL_CREATE_TRANSACTION);

        }
        catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    // Fetch accounts from API in JSON form
    List<Account> fetchAccounts() {
        try {
            URL url = new URL("https://api.asep-strath.co.uk/api/accounts");
            return mapper.readValue(url, new TypeReference<List<Account>>() {});
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    List<Business> fetchBusinesses() {
        try{
            URL url = new URL("https://api.asep-strath.co.uk/api/businesses");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            List<Business> businesses = new ArrayList<>();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            try(in){
                String inputLine;
                in.readLine();

                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.trim().isEmpty())
                        continue;

                    String[] input_fields = inputLine.split(",");
                    if (input_fields.length >= 4){
                        String id = input_fields[0].trim();
                        String name = input_fields[1].trim();
                        String category = input_fields[2].trim();
                        boolean Sanctioned = Boolean.parseBoolean(input_fields[3].trim());

                        businesses.add(new Business(id, name, category, Sanctioned));
                    }
                }
                return businesses;
            }
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    List<Transaction> fetchTransactions() {
        try {
            URL url = new URL("https://api.asep-strath.co.uk/api/transactions");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            XmlMapper mapper = new XmlMapper();
            XmlParser pageResult = mapper.readValue(con.getInputStream(), XmlParser.class);
            return pageResult.getTransactions();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}