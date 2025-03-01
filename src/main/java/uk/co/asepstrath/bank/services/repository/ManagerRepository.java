package uk.co.asepstrath.bank.services.repository;

import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.Manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManagerRepository extends BaseRepository {

    public ManagerRepository(Logger logger) {
        super(logger);
    }

    private static final String SQL_CREATE_TABLE = """
        CREATE TABLE Managers (
            ManagerID VARCHAR(255) NOT NULL,
            Name VARCHAR(255) NOT NULL,
            PRIMARY KEY (ManagerID)
        )
        """;


    private static final String SQL_INSERT_MANAGER =
            "INSERT INTO Managers (ManagerID, Name) VALUES (?, ?)";

    private static final String SQL_SELECT_ALL_ACCOUNTS =
            "SELECT AccountID, Name, Balance, RoundUpEnabled FROM Accounts";

    public void createTable(Connection connection) throws SQLException {
        executeUpdate(connection, SQL_CREATE_TABLE);
    }

    public void insert(Connection connection, Manager manager) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT_MANAGER)) {
            statement.setString(1, manager.getManagerID());
            statement.setString(2, manager.getName());
            statement.executeUpdate();
        }
    }

    public List<Account> getAllAccounts(Connection connection) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(SQL_SELECT_ALL_ACCOUNTS);
            while (resultSet.next()) {
                accounts.add(
                        new Account(
                            resultSet.getString("AccountID"),
                            resultSet.getString("Name"),
                            resultSet.getBigDecimal("Balance"),
                            resultSet.getBoolean("RoundUpEnabled")
                    )
                );
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return accounts;
    }
}
