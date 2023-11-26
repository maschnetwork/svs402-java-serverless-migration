// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.serverlessflix.claps;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotifyCreator implements RequestHandler<NotifyCreator.VideoNotification, Void > {

    private final Logger logger = LoggerFactory.getLogger(NotifyCreator.class);
    private final ClapsService clapsService;

    public NotifyCreator(){
        this.clapsService = new ClapsService(System.getenv("TABLE_NAME"));
    }


    @Override
    public Void handleRequest(VideoNotification videoNotification, Context context) {
        //Logged to CloudWatch Logs - Could trigger any kind of notification here (SNS, SES etc.)
        var video = clapsService.getVideo(videoNotification.video);
        logger.info("Congratulations {}! Your video received {} claps since creation!",
                videoNotification.authorUsername, video.claps());

        return null;
    }

    public record VideoNotification(String video, String authorUsername, String authorEmail){};
}
