package software.serverlessflix.claps;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewVideoHandler implements RequestHandler<NotifyVideoCreator, Void> {

    private static final Logger logger = LoggerFactory.getLogger(NewVideoHandler.class);
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private final ClapsService clapsService;

    public NewVideoHandler() {
        this.clapsService = new ClapsService(System.getenv("TABLE_NAME"));
    }

    @Override
    public Void handleRequest(NotifyVideoCreator input, Context context) {
        logger.info("Event: " + event);

        return null;
    }
}
