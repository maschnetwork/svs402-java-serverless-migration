package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.LambdaRuntime;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Handler for requests to Lambda function.
 */
public class ScheduledJob implements RequestStreamHandler {

    private final LambdaLogger logger = LambdaRuntime.getLogger();
    private final ObjectMapper mapper;

    public ScheduledJob() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        EventBridgeSchedulerEvent event = mapper.readValue(input, EventBridgeSchedulerEvent.class);
        logger.log("Event: " + event);
    }
}
