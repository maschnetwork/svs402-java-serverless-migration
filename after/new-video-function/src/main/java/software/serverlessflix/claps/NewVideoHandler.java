package software.serverlessflix.claps;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.services.scheduler.SchedulerAsyncClient;
import software.amazon.awssdk.services.scheduler.model.CreateScheduleResponse;
import software.serverlessflix.claps.domain.Video;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        var event = objectMapper.readValue(input, new TypeReference<EventBridgeEvent<Video>>(){});
        var video = event.detail();

        try {
            this.clapsService.createVideo(video);
        } catch (UnableToSaveException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

   /*     var dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String oneHourFromNow = dateFormat.format(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()));

        CreateScheduleRequest scheduleRequest = CreateScheduleRequest.builder()
                .name(video.author().username() + "-1m-video-nofitication")
                .startDate(Instant.now())
                .scheduleExpression(String.format("at(%S)", oneHourFromNow))
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
                .build();*/

     /*   try {
            // @TODO do we need to consider partial failure?
            CreateScheduleResponse createScheduleResponse = this.schedulerAsyncClient.createSchedule(scheduleRequest).get();
            logger.info("schedule arn: {}", createScheduleResponse.scheduleArn());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } */
    }
}
