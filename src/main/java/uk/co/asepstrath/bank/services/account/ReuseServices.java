package uk.co.asepstrath.bank.services.account;

import org.slf4j.Logger;
import uk.co.asepstrath.bank.Account;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/*
 *  Remove duplications in code from the 'services class'
 */

public class ReuseServices {

    private final DataSource dataSource;
    private final Logger logger;

    public ReuseServices(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;
    }

    void putBalanceInModel(Map<String, Object> model, String accountId) {
        BigDecimal balance = BigDecimal.ZERO;
        try(PreparedStatement statement = dataSource.getConnection().prepareStatement("select Balance from Accounts where AccountID = ?")) {
            statement.setString(1, accountId);
            ResultSet rs = statement.executeQuery();
            try(rs){
                if (rs.next()) {
                    balance = rs.getBigDecimal("Balance");
                    logger.info("Account balance: {}" , balance);
                }else{
                    logger.info("Account balance is empty");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        model.put("balance", balance);
    }

    void updateDatabaseBalance(Account account) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE Accounts SET Balance = ? WHERE AccountID = ?")) {

            statement.setBigDecimal(1, account.getBalance());
            statement.setString(2, account.getAccountID());
            statement.executeUpdate();
        }
    }
}