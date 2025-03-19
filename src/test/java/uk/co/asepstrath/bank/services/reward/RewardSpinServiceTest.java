package uk.co.asepstrath.bank.services.reward;

import io.jooby.Context;
import io.jooby.Session;
import io.jooby.ValueNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.asepstrath.bank.Reward;
import uk.co.asepstrath.bank.services.data.RewardDataService;
import uk.co.asepstrath.bank.services.repository.RewardRepository;
import java.lang.reflect.*;

import javax.sql.DataSource;

import org.slf4j.Logger;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static uk.co.asepstrath.bank.Constants.SESSION_ACCOUNT_ID;

class RewardSpinServiceTest {
    @Mock
    private DataSource dataSource;
    @Mock
    private Logger logger;
    @Mock
    private RewardRepository rewardRepository;
    @Mock
    private RewardDataService rewardDataService;
    @Mock
    private Context mockContext;
    @Mock
    private Session mockSession;
    private RewardSpinService rewardSpinService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rewardSpinService = spy(new RewardSpinService(dataSource, logger, rewardRepository));
        rewardSpinService.rewardRepository = rewardRepository;
        when(mockContext.session()).thenReturn(mockSession);
        ValueNode accountID = mock(ValueNode.class);
        when(mockSession.get(SESSION_ACCOUNT_ID)).thenReturn(accountID);
        when(accountID.value()).thenReturn("acc123");

    }

    @Test
    void tsetProcessSpinSuccess() throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Reward reward = new Reward("ASUS TUF", "Gaming Laptop", new BigDecimal(3500), 50.00);
        List<Reward> rewards = List.of(reward);

        when(rewardRepository.getAllRewards(any())).thenReturn(rewards);
        rewardSpinService.processSpin(mockContext);

        doNothing().when(rewardSpinService).addMessageToSession(any(),anyString(),anyString());
        verify(rewardRepository, times(1)).getAllRewards(any());
    }

    @Test
    void testSelectRewardRandom() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Reward reward = new Reward("ASUS TUF", "Gaming Laptop", new BigDecimal(3500), 50.00);
        Reward reward1 = new Reward("Vacation trip", "Trip to Dubai", new BigDecimal(5000), 20.00);
        Reward reward2 = new Reward("DisneyLand", "Disney Land Tickets", new BigDecimal(900),30.00);
        List<Reward> rewards = Arrays.asList(reward, reward1, reward2);

        SecureRandom secureRandom = mock(SecureRandom.class);
        when(secureRandom.nextDouble()).thenReturn(0.6); // should return Reward 1

        Method selectRewardMethod = RewardSpinService.class.getDeclaredMethod("selectWeightedRandomReward", List.class);
        selectRewardMethod.setAccessible(true);
        Reward selectReward = (Reward) selectRewardMethod.invoke(rewardSpinService, rewards);

        assertEquals(reward, selectReward);

    }
}