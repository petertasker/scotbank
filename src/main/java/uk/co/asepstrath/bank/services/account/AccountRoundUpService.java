package uk.co.asepstrath.bank.services.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;

import io.jooby.Context;
import uk.co.asepstrath.bank.Account;
import uk.co.asepstrath.bank.services.repository.AccountRepository;

public class AccountRoundUpService extends AccountService {

    private final AccountRepository accountRepository;

    public AccountRoundUpService(DataSource dataSource, Logger logger) {
        super(dataSource, logger);
        accountRepository = new AccountRepository(logger);
    }

    /*
     *  Process round up toggle
     */
    /*
     *  Update Round Up toggle
     */
    public void toggleDatabaseRoundUp(Context ctx) throws SQLException {
        String accountId = getAccountIdFromSession(ctx);
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(
                "UPDATE Accounts SET RoundUpEnabled = NOT RoundUpEnabled WHERE AccountID = ?")) {
            statement.setString(1, accountId);
            statement.executeUpdate();
            Account account = accountRepository.getAccount(connection, accountId);
            logger.info(accountId + " Round Up is now " + account.isRoundUpEnabled());
        }
    }
}
