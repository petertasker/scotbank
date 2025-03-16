package uk.co.asepstrath.bank.controllers;

import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.annotation.GET;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.services.rewards.RewardFetchService;
import uk.co.asepstrath.bank.services.rewards.RewardSpinService;
import uk.co.asepstrath.bank.services.repository.RewardsRepository;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/rewards")
public class RewardsController {
    private final RewardFetchService rewardFetchService;
    private final RewardSpinService rewardSpinService;
    private final RewardsRepository rewardsRepository;

    public RewardsController(DataSource datasource, Logger logger) {
        this.rewardFetchService = new RewardFetchService(datasource, logger);
        this.rewardSpinService = new RewardSpinService(datasource, logger);
        this.rewardsRepository = new RewardsRepository(logger);
    }

    /**
     * Display the rewards page
     */
    @GET
    public ModelAndView rewardsPage(Context ctx) throws SQLException {
        String userId = ctx.session().get("AccountID").value();

        List<Map<String, Object>> rewards = rewardFetchService.fetchAndStoreRewards();
        String rewardHistory = rewardSpinService.getUserRewardHistory(userId);

        Map<String, Object> model = new HashMap<>();
        model.put("Rewards", rewards);
        model.put("rewardHistory", rewardHistory);

        return new ModelAndView("rewards.hbs", model);
    }

    /**
     * Handle the spin logic
     */
    @POST
    @Path("/spin")
    public ModelAndView spinWheel(Context ctx) throws SQLException {
        String userId = ctx.session().get("AccountID").value();
        String spinResult = rewardSpinService.processSpin(userId);

        List<Map<String, Object>> rewards = rewardFetchService.getRewardsFromDatabase();
        String rewardHistory = rewardSpinService.getUserRewardHistory(userId);

        Map<String, Object> model = new HashMap<>();
        model.put("Rewards", rewards);
        model.put("rewardHistory", rewardHistory);
        model.put("spinResult", spinResult); // Show what the user won

        return new ModelAndView("rewards.hbs", model);
    }
}
