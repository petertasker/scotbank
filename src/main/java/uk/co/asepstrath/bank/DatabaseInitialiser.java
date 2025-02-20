package uk.co.asepstrath.bank;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseInitialiser {

    private static final String SQL_CREATE_ACCOUNT = """
        CREATE TABLE Accounts (
            AccountID varchar(255) NOT NULL,
            Balance DECIMAL NOT NULL,
            Name varchar(255) NOT NULL,
            RoundUpEnabled BIT NOT NULL,
            PRIMARY KEY (AccountID)
        )
    """;

    private static final String SQL_CREATE_BUSINESS = """
            CREATE TABLE Business (
            BusinessID varchar(255) NOT NULL,
            Business_Name varchar(225) NOT NULL,
            Category varchar(255) NOT NULL,
            Sanctioned BIT NOT NULL,
            PRIMARY KEY (BusinessID)
            )
            """;

    private static final String SQL_INSERT_ACCOUNT = """
        INSERT INTO Accounts (AccountID, Balance, Name, RoundUpEnabled) VALUES (?, ?, ?, ?)""";

    private static final String SQL_INSERT_BUSINESS = """
            INSERT INTO Business (BusinessID, Business_Name, Category, Sanctioned ) VALUES (?, ?, ?, ?)""";

    private final DataSource dataSource;
    Logger log = LoggerFactory.getLogger(DatabaseInitialiser.class);
    private final ObjectMapper mapper;

    public DatabaseInitialiser(DataSource dataSource) {
        this.dataSource = dataSource;
        this.mapper = new ObjectMapper();
    }

    public void initialise() throws SQLException {
        try {
            Connection connection = dataSource.getConnection();
            createAccountTable(connection);
            createBusinessTable(connection);
        }
            catch (SQLException e) {
                throw new SQLException(e);
            }
    }

    private void createAccountTable(Connection connection) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(SQL_CREATE_ACCOUNT);
            List<Account> accounts = fetchAccounts();
            for (Account account : accounts) {
                insertAccount(connection, account);
            }
        }
        catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    private void createBusinessTable(Connection connection) throws SQLException {
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate(SQL_CREATE_BUSINESS);
            List<Business> businesses = fetchBusinesses();
            for(Business business : businesses) {
                insertBusiness(connection, business);
            }
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
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    // Insert account into database
    private void insertAccount(Connection connection, Account account) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_ACCOUNT);
            preparedStatement.setString(1, account.getAccountID());
            preparedStatement.setBigDecimal(2, account.getBalance());
            preparedStatement.setString(3, account.getName());
            preparedStatement.setBoolean(4, account.isRoundUpEnabled());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    private void insertBusiness(Connection connection, Business business) throws SQLException {
        try(Statement statement = connection.createStatement()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_BUSINESS);
            preparedStatement.setString(1, business.getID());
            preparedStatement.setString(2, business.getName());
            preparedStatement.setString(3, business.getCategory());
            preparedStatement.setBoolean(4,business.isSanctioned());
            preparedStatement.executeUpdate();
        }catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public List<Account> queryAccounts() throws SQLException {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Accounts");
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Account> accounts = new ArrayList<>();

            while (resultSet.next()) {
                accounts.add(new Account(
                        resultSet.getString("AccountID"),
                        resultSet.getString("Name"),
                        resultSet.getBigDecimal("Balance"),
                        resultSet.getBoolean("RoundUpEnabled")));
                log.info("Added account: {}", resultSet.getString("AccountID"));
            }

            return accounts;
        }
        catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public List<Business> queryBusinesses() throws SQLException {
        try{
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Business");
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Business> businesses = new ArrayList<>();

            while (resultSet.next()) {
                businesses.add(new Business(
                        resultSet.getString("BusinessID"),
                        resultSet.getString("Business_Name"),
                        resultSet.getString("Category"),
                        resultSet.getBoolean("Sanctioned")));
                log.info("Added business: {}",
                        resultSet.getString("BusinessID"));
            }
            return businesses;

        }catch (SQLException e) {
            throw new SQLException(e);
        }
    }
}