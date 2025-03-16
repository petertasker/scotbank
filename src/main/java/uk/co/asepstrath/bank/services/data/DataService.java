package uk.co.asepstrath.bank.services.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import uk.co.asepstrath.bank.services.BaseService;

public class DataService extends BaseService {

    protected final UnirestWrapper unirestWrapper;
    protected final ObjectMapper objectMapper;

    public DataService(Logger logger, UnirestWrapper unirestWrapper, ObjectMapper objectMapper) {
        super(logger);
        this.unirestWrapper = unirestWrapper;
        this.objectMapper = objectMapper;
    }
}