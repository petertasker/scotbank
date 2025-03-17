package uk.co.asepstrath.bank.services.reward;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jooby.Context;
import io.jooby.Session;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Reward;
import uk.co.asepstrath.bank.services.BaseService;
import uk.co.asepstrath.bank.services.data.RewardDataService;
import uk.co.asepstrath.bank.services.data.UnirestWrapper;
import uk.co.asepstrath.bank.services.repository.RewardRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static uk.co.asepstrath.bank.Constants.*;

public class RewardSpinService extends RewardService {

    private final RewardRepository rewardRepository;
    private final RewardDataService rewardDataService;

    public RewardSpinService(DataSource datasource, Logger logger) {
        super(datasource, logger);
        this.rewardRepository = new RewardRepository(logger);
        this.rewardDataService = new RewardDataService(logger, new UnirestWrapper(), new ObjectMapper(), datasource);
    }

    /**
     * Process the user spinning the wheel (user MUST win a reward)
     */
    public void processSpin(Context ctx) throws SQLException {
        List<Reward> rewards = rewardRepository.getAllRewards(getConnection());
        Reward selected = selectWeightedRandomReward(rewards);
        assert selected != null; // This might fail on a flop error???

        Session session = getSession(ctx);
        String accountId = String.valueOf(session.get(SESSION_ACCOUNT_ID));
        try {
            rewardDataService.postReward(selected, accountId);
            addMessageToSession(ctx, SESSION_SUCCESS_MESSAGE, "A " + selected.getName() + " has been added to your account");
        }
        catch (Exception e) {
            addMessageToSession(ctx, SESSION_ERROR_MESSAGE, "An upstream error occurred while adding a reward to your account");
        }
        redirect(ctx, ROUTE_REWARD);
    }

    /**
     * Selects a reward based on weighted probability
     */
    private Reward selectWeightedRandomReward(List<Reward> rewards) {
        double totalWeight = 0.0;
        for (Reward reward : rewards) {
            totalWeight += reward.getChance();
        }

        double random = Math.random() * totalWeight;

        // Find the reward that corresponds to the random value
        double weightSum = 0.0;
        for (Reward reward : rewards) {
            weightSum += reward.getChance();
            if (random < weightSum) {
                return reward;
            }
        }

        // Fallback (shouldn't happen unless list is empty)
        return rewards.isEmpty() ? null : rewards.getLast();
    }
}
