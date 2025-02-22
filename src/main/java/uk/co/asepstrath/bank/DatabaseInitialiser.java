package uk.co.asepstrath.bank;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.sql.DataSource;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.sql.*;
import java.util.*;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.asepstrath.bank.parsers.XmlParser;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import com.fasterxml.jackson.core.JsonParseException;


public class DatabaseInitialiser {

    private static final String SQL_CREATE_ACCOUNT = """
        CREATE TABLE Accounts (
            AccountID VARCHAR(255) NOT NULL,
            Balance DECIMAL(12,2) NOT NULL,
            Name VARCHAR(255) NOT NULL,
            RoundUpEnabled BIT NOT NULL,
            PRIMARY KEY (AccountID)
        )
    """;

    private static final String SQL_CREATE_BUSINESS = """
        CREATE TABLE Businesses (
            BusinessID VARCHAR(255) NOT NULL,
            BusinessName VARCHAR(255) NOT NULL,
            Category VARCHAR(255) NOT NULL,
            Sanctioned BIT NOT NULL,
            PRIMARY KEY (BusinessID)
        )
    """;

    private static final String SQL_CREATE_TRANSACTION = """
        CREATE TABLE Transactions (
            Timestamp DATETIME NOT NULL,
            Amount DECIMAL(12,2) NOT NULL,
            SenderID VARCHAR(255) NOT NULL,
            TransactionID VARCHAR(255) NOT NULL,
            ReceiverID VARCHAR(255) NOT NULL,
            TransactionType VARCHAR(255) NOT NULL,
            PRIMARY KEY (TransactionID),
            FOREIGN KEY (ReceiverID) REFERENCES Businesses(BusinessID),
            FOREIGN KEY (SenderID) REFERENCES Accounts(AccountID)
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
        dbHandler = new DatabaseHandler();
    }

    public void initialise() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
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
        }
        catch (XMLStreamException | JsonParseException e) {
            throw new SQLException("Fetching failed somewhere", e);
        } catch (IOException e) {
            throw new SQLException("Database creation failed", e);
        }
    }


    private void createAccountTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(SQL_CREATE_ACCOUNT);
        }
    }

    private void createBusinessTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(SQL_CREATE_BUSINESS);
        }
    }

    private void createTransactionTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(SQL_CREATE_TRANSACTION);
        }
    }

    // Fetch accounts from API in JSON form
    // Replaced Deprecated package (java.net.URL.URL) with UniRest instead
    
    List<Account> fetchAccounts() throws JsonParseException {
        try {
            HttpResponse<String> response = Unirest.get("https://api.asep-strath.co.uk/api/accounts").asString();
            
            if (response.isSuccess()) {
                return mapper.readValue(response.getBody(), new TypeReference<List<Account>>() {});
            } else {
                throw new JsonParseException(null, "Failed to fetch accounts: " + response.getStatus());
            }
        } catch (IOException e) {
            throw new JsonParseException(null, "Failed to parse account data");
        }
    }

    List<Business> fetchBusinesses() throws IOException {
        try {
            HttpResponse<String> response = Unirest.get("https://api.asep-strath.co.uk/api/businesses").asString();

            if (!response.isSuccess()) {
                throw new IOException("Failed to fetch businesses: " + response.getStatus());
            }

            List<Business> businesses = new ArrayList<>();
            String[] lines = response.getBody().split("\n");
            boolean skipHeader = true;
            
            for (String inputLine : lines) {
                if (skipHeader){
                    skipHeader = false;
                    continue;
                }

                String[] inputFields = inputLine.split(",");
                if (inputFields.length >= 4) {
                    String id = inputFields[0].trim();
                    String name = inputFields[1].trim();
                    String category = inputFields[2].trim();
                    boolean sanctioned = Boolean.parseBoolean(inputFields[3].trim());

                    businesses.add(new Business(id, name, category, sanctioned));
                }
            }
            return businesses;
            
        } catch (Exception e) {
            throw new IOException("Failed to parse business data: ", e);
        }
    }

    List<Transaction> fetchTransactions() throws XMLStreamException {
        try {
            HttpResponse<String> response = Unirest.get("https://api.asep-strath.co.uk/api/transactions").asString();

            if (!response.isSuccess()) {
                throw new XMLStreamException("Failed to fetch transactions: " + response.getStatus());
            }

            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.registerModule(new JodaModule());
            XmlParser pageResult = xmlMapper.readValue(response.getBody(), XmlParser.class);
            return pageResult.getTransactions();
        } catch (IOException e) {
            throw new XMLStreamException("Failed to parse XML", e);
        }
    }
}