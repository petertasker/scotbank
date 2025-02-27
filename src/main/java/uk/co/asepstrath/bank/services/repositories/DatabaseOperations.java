package uk.co.asepstrath.bank.services.repositories;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseOperations {
    void createTables(Connection connection) throws SQLException;
    void insertData(Connection connection) throws SQLException, IOException, XMLStreamException;
}
