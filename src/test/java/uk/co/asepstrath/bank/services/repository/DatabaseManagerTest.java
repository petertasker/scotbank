package uk.co.asepstrath.bank.services.repository;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.Business;
import uk.co.asepstrath.bank.Manager;
import uk.co.asepstrath.bank.Transaction;
import uk.co.asepstrath.bank.services.data.*;

import javax.sql.DataSource;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

import static org.mockito.Mockito.*;


class DatabaseManagerTest {
    @Mock
    private Logger log;
    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;
    @Mock
    private Statement statement;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private BusinessRepository businessRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ManagerRepository managerRepository;
    @Mock
    private AccountDataService accountDataService;
    @Mock
    private BusinessDataService businessDataService;
    @Mock
    private TransactionDataService transactionDataService;
    @Mock
    private ManagerDataService managerDataService;
    private DatabaseManager databaseManager;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(dataSource.getConnection()).thenReturn(connection);

        databaseManager = spy( new DatabaseManager(dataSource, log));

        databaseManager = spy(databaseManager);
    }
}