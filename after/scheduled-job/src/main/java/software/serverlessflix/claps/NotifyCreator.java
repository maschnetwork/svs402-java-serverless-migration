// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.serverlessflix.claps;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import software.serverlessflix.claps.domain.Video;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NotifyCreator implements RequestHandler<NotifyCreator.VideoNotification, Void > {

    @Override
    public Void handleRequest(VideoNotification videoNotification, Context context) {
        System.out.println(videoNotification);

        return null;
    }

    public record VideoNotification(String video, String authorUsername, String authorEmail){};
}
