package software.reinvent.serverlessflix.claps;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.reinvent.serverlessflix.claps.domain.Video;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NewVideoHandler implements RequestStreamHandler {

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
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        EventBridgeSchedulerEvent event = objectMapper.readValue(input, EventBridgeSchedulerEvent.class);
//        Video video = (Video) event.detail();
        logger.info("Event: " + event);

//        this.clapsService.createVideo(video);
    }
}
