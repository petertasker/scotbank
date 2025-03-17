package uk.co.asepstrath.bank.services.reward;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.Session;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Reward;
import uk.co.asepstrath.bank.services.repository.RewardRepository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.co.asepstrath.bank.Constants.*;

public class RewardViewService extends RewardService {

    private RewardRepository rewardRepository;

    public RewardViewService(DataSource dataSource, Logger logger) {
        super(dataSource, logger);
        rewardRepository = new RewardRepository(logger);
    }

    public ModelAndView<Map<String, Object>> viewRewardPage(Context ctx) throws SQLException {
        Map<String, Object> model = createModel();
        transferSessionAttributeToModel(ctx, SESSION_SUCCESS_MESSAGE, model);
        transferSessionAttributeToModel(ctx, SESSION_ERROR_MESSAGE, model);
        List<Reward> rewards = rewardRepository.getAllRewards(getConnection());
        model.put("rewards", rewards);
        return new ModelAndView<>(TEMPLATE_REWARDS, model);

    }
}
