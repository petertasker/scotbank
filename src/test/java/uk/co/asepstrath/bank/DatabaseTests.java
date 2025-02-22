package uk.co.asepstrath.bank;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.jooby.test.JoobyTest;
import io.jooby.test.MockRouter;

import jakarta.inject.Inject;
import org.junit.jupiter.api.*;
import javax.sql.DataSource;
import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static org.junit.jupiter.api.Assertions.*;

@JoobyTest(App.class)
class DatabaseTests {
    @Inject
    private DataSource dataSource;
    private DatabaseInitialiser databaseInitialiser;
    private Connection connection;

    @BeforeAll
    static void setupClass() {
        new MockRouter(new App());
    }

    @BeforeEach
    void setUp() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");

        dataSource = new HikariDataSource(config);
        connection = dataSource.getConnection();

        assertNotNull(dataSource, "DataSource should not be null");
        databaseInitialiser = new DatabaseInitialiser(dataSource);
        databaseHandler = new DatabaseHandler();

        cleanDatabase();
        databaseInitialiser.initialise();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
        }
    }

    private void cleanDatabase() throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS Transactions");
            statement.execute("DROP TABLE IF EXISTS Accounts");
            statement.execute("DROP TABLE IF EXISTS Businesses");
        }
    }

    @Test
    void testTablesExist() throws SQLException {
        try (var rs = connection.getMetaData().getTables(null, null, "%", null)) {
            var tables = new java.util.ArrayList<String>();
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME").toLowerCase());
            }
            assertTrue(tables.contains("accounts"));
            assertTrue(tables.contains("businesses"));
            assertTrue(tables.contains("transactions"));
        }
    }
}