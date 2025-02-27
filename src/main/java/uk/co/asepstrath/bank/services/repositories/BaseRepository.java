package uk.co.asepstrath.bank.services.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

abstract class BaseRepository {
    protected final Logger logger;

    public BaseRepository(Logger logger) {
        this.logger = LoggerFactory.getLogger(BaseRepository.class);
    }

    protected void executeUpdate(Connection connection, String sql) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }
}
