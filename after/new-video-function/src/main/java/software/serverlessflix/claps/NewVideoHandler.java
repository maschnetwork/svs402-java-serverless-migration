package software.serverlessflix.claps;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.services.scheduler.SchedulerAsyncClient;
import software.amazon.awssdk.services.scheduler.model.ActionAfterCompletion;
import software.amazon.awssdk.services.scheduler.model.CreateScheduleRequest;
import software.amazon.awssdk.services.scheduler.model.CreateScheduleResponse;
import software.amazon.awssdk.services.scheduler.model.FlexibleTimeWindow;
import software.amazon.awssdk.services.scheduler.model.FlexibleTimeWindowMode;
import software.amazon.awssdk.services.scheduler.model.Target;
import software.serverlessflix.claps.domain.Video;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class NewVideoHandler implements RequestStreamHandler {

    private static final Logger logger = LoggerFactory.getLogger(NewVideoHandler.class);
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private final ClapsService clapsService;
    private final SchedulerAsyncClient schedulerAsyncClient;
    private final String creatorNotificationTarget;
    private final String creatorNotificationRoleArn;

    public NewVideoHandler() {
        this.clapsService = new ClapsService(System.getenv("TABLE_NAME"));
        this.creatorNotificationTarget = System.getenv("CREATOR_NOTIFICATION_TARGET");
        this.creatorNotificationRoleArn = System.getenv("CREATOR_NOTIFICATION_ROLE_ARN");
        this.schedulerAsyncClient = SchedulerAsyncClient.builder()
                .httpClient(AwsCrtAsyncHttpClient.create())
                .build();
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        EventBridgeSchedulerEvent<Map<String, Object>> event = objectMapper.readValue(input, EventBridgeSchedulerEvent.class);

        // @TODO could improve this serialization story even more
        Video video = objectMapper.convertValue(event.detail().get("video"), Video.class);

        try {
            this.clapsService.createVideo(video);
        } catch (UnableToSaveException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

        // @TODO do we need to consider partial failure?
        CreateScheduleRequest scheduleRequest = CreateScheduleRequest.builder()
                .name(video.author().username() + "-1m-video-nofitication")
                .startDate(Instant.now().plus(1, ChronoUnit.MINUTES))
                .flexibleTimeWindow(FlexibleTimeWindow.builder()
                        .mode(FlexibleTimeWindowMode.OFF)
                        .build())
                .actionAfterCompletion(ActionAfterCompletion.DELETE)
                .target(Target.builder()
                        .arn(this.creatorNotificationTarget)
                        .roleArn(this.creatorNotificationRoleArn)
                        .input("""
                                {
                                    "video":"12345",
                                    "author":"mememe"
                                }
                                """)
                        .build())
                .build();

        try {
            CreateScheduleResponse createScheduleResponse = this.schedulerAsyncClient.createSchedule(scheduleRequest).get();
            logger.info("schedule arn: {}", createScheduleResponse.scheduleArn());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
