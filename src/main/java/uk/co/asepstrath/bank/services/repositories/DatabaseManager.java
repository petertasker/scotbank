package uk.co.asepstrath.bank.services.repositories;

import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.Business;
import uk.co.asepstrath.bank.Transaction;
import uk.co.asepstrath.bank.services.data.AccountDataService;
import uk.co.asepstrath.bank.services.data.BusinessDataService;
import uk.co.asepstrath.bank.services.data.DataService;
import uk.co.asepstrath.bank.services.data.TransactionDataService;

import javax.sql.DataSource;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DatabaseManager implements DatabaseOperations {

    private final DataSource dataSource;
    private final Logger logger;

    private final AccountRepository accountRepository;
    private final BusinessRepository businessRepository;
    private final TransactionRepository transactionRepository;

    private final DataService<Account> accountDataService;
    private final DataService<Business> businessDataService;
    private final DataService<Transaction> transactionDataService;

    public DatabaseManager(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;

        this.accountRepository = new AccountRepository(logger);
        this.businessRepository = new BusinessRepository(logger);
        this.transactionRepository = new TransactionRepository(logger, accountRepository);

        this.accountDataService = new AccountDataService();
        this.businessDataService = new BusinessDataService();
        this.transactionDataService = new TransactionDataService();

    }

    public void initialise() throws SQLException, IOException, XMLStreamException {
        try (Connection connection = dataSource.getConnection()) {
            createTables(connection);
            insertData(connection);
        } catch (SQLException | IOException | XMLStreamException e) {
            logger.error("Database initialization failed", e);
            throw e;
        }
    }

    @Override
    public void createTables(Connection connection) throws SQLException {
        accountRepository.createTable(connection);
        logger.info("Account table created");

        businessRepository.createTable(connection);
        logger.info("Business table created");

        transactionRepository.createTable(connection);
        logger.info("Transaction table created");
    }

    @Override
    public void insertData(Connection connection) throws SQLException, IOException, XMLStreamException {
        // Insert accounts
        List<Account> accounts = accountDataService.fetchData();
        for (Account account : accounts) {
            accountRepository.insert(connection, account);
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
    }

}
