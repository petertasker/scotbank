package uk.co.asepstrath.bank.services.reward;

import org.slf4j.Logger;
import uk.co.asepstrath.bank.services.BaseService;

import javax.sql.DataSource;

public class RewardService extends BaseService {
    public RewardService(DataSource dataSource, Logger logger) {
        super(dataSource, logger);
    }
}
