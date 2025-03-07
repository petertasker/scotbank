package uk.co.asepstrath.bank.services.repository;

import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.Business;
import uk.co.asepstrath.bank.Manager;
import uk.co.asepstrath.bank.Transaction;
import uk.co.asepstrath.bank.services.data.*;

import javax.sql.DataSource;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

import static uk.co.asepstrath.bank.Constants.DEFAULT_PASSWORD;

/**
 * The manager for all repository interactions
 */
public class DatabaseManager implements DatabaseOperations {

    private final DataSource dataSource;
    private final Logger logger;

    private final AccountRepository accountRepository;
    private final BusinessRepository businessRepository;
    private final TransactionRepository transactionRepository;
    private final ManagerRepository managerRepository;

    private final DataService<Account> accountDataService;
    private final DataService<Business> businessDataService;
    private final DataService<Transaction> transactionDataService;
    private final DataService<Manager> managerDataService;

    public DatabaseManager(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;

        this.accountRepository = new AccountRepository(logger);
        this.businessRepository = new BusinessRepository(logger);
        this.transactionRepository = new TransactionRepository(logger, accountRepository);
        this.managerRepository = new ManagerRepository(logger);


        this.accountDataService = new AccountDataService();
        this.businessDataService = new BusinessDataService();
        this.transactionDataService = new TransactionDataService();
        this.managerDataService = new ManagerDataService();

    }

    /**
     * Instantiates the creation and insertion of API data
     *
     * @throws SQLException If database connection or queries fail
     * @throws IOException If API parsing fails
     * @throws XMLStreamException If XML parsing fails
     */
    public void initialise() throws SQLException, IOException, XMLStreamException {
        try (Connection connection = dataSource.getConnection()) {
            createTables(connection);
            insertData(connection);
        }
    }


    /**
     * Creates all the tables used in the database
     * @param connection Database connection
     * @throws SQLException Database connection failure
     */
    @Override
    public void createTables(Connection connection) throws SQLException {
        accountRepository.createTable(connection);
        logger.info("Account table created");

        businessRepository.createTable(connection);
        logger.info("Business table created");

        transactionRepository.createTable(connection);
        logger.info("Transaction table created");

        managerRepository.createTable(connection);
        logger.info("Manager table created");
    }

    /**
     * Inserts initial API data into the database
     * @param connection Database connection
     * @throws SQLException Database connection failure
     * @throws IOException API parsing failure
     * @throws XMLStreamException API parsing failure
     */
    @Override
    public void insertData(Connection connection) throws SQLException, IOException, XMLStreamException {
        // Insert accounts
        List<Account> accounts = accountDataService.fetchData();
        for (Account account : accounts) {
            try {
                String password = generatePassword(account.getAccountID());
                accountRepository.insert(connection, account,password);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new SQLException("Unable to insert account");
            }
        }
        logger.info("Accounts inserted");

        // Insert businesses
        List<Business> businesses = businessDataService.fetchData();
        for (Business business : businesses) {
            businessRepository.insert(connection, business);
        }
        logger.info("Businesses inserted");

        // Insert transactions
        List<Transaction> transactions = transactionDataService.fetchData();
        for (Transaction transaction : transactions) {
            transactionRepository.insert(connection, transaction);
        }
        logger.info("Transactions inserted");

        // Insert some hard coded managers
        List<Manager> managers = managerDataService.fetchData();
        for (Manager manager : managers) {
            managerRepository.insert(connection, manager);
        }
        logger.info("Managers inserted");
    }

    public static String generatePassword(String accountID) throws NoSuchAlgorithmException {
        String plainTextPassword = accountID + DEFAULT_PASSWORD;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(plainTextPassword.getBytes(StandardCharsets.UTF_8));

        String encodedHash = Base64.getEncoder().withoutPadding().encodeToString(hash);

        return "Psw@" + encodedHash.substring(0,8) + "$$";
    }

}
