package uk.co.asepstrath.bank.services.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.core.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.Reward;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RewardDataServiceTest {

    private RewardDataService rewardDataService ;
    private UnirestWrapper unirestWrapper;
    private Logger logger;
    private ObjectMapper objectMapper;
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        unirestWrapper = mock(UnirestWrapper.class);
        logger = mock(Logger.class);
        objectMapper = new ObjectMapper();
        dataSource = mock(DataSource.class);
        rewardDataService = new RewardDataService(logger, unirestWrapper, objectMapper, dataSource);
    }

    @Test
    void testFetchDataSuccess() throws IOException {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.isSuccess()).thenReturn(true);
        when(mockResponse.getBody()).thenReturn("""
                [
                    {
                    "name": "Sim Racing Kit",
                    "description": "The best experience you will ever have",
                    "value": 5000,
                    "chance": 5.001
                    }
                
                ]
                """);

        when(unirestWrapper.get("https://api.asep-strath.co.uk/api/rewards")).thenReturn(mockResponse);
        List<Reward> rewards = rewardDataService.fetchData();

        assertNotNull(rewards);
        assertEquals(1, rewards.size());
        assertEquals("Sim Racing Kit", rewards.getFirst().getName());
        assertEquals("The best experience you will ever have", rewards.getFirst().getDescription());
        assertEquals(5000,rewards.getFirst().getValue().intValue());
        assertEquals(5.001,rewards.getFirst().getChance());
    }

    @Test
    void testFetchDataFailure(){
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.isSuccess()).thenReturn(false);

        when(unirestWrapper.get(anyString())).thenReturn(mockResponse);
        assertThrows(IOException.class, () -> rewardDataService.fetchData());
    }
}