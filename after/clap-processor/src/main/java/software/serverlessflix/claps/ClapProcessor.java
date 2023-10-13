// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.serverlessflix.claps;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.jr.ob.JSON;

import java.io.IOException;

import static java.util.stream.Collectors.groupingBy;

public class ClapProcessor implements RequestHandler<SQSEvent, SQSBatchResponse> {


    @Override
    public SQSBatchResponse handleRequest(SQSEvent sqsEvent, Context context) {
        var videos = sqsEvent.getRecords()
                .stream()
                .map(this::clapNotificationFromSQSBody)
                .collect(groupingBy(ClapNotification::video));

        //Todo: Sending BatchExecuteStatement via ClapService
        System.out.println(videos);

        return null;
    }

    private ClapNotification clapNotificationFromSQSBody(SQSEvent.SQSMessage sqsMessage){
        try {
             return new ClapNotification(JSON.std.mapFrom(sqsMessage.getBody()).get("video").toString());
        } catch (IOException e) {
           throw new RuntimeException("Unable to process JSON of SQSMessage", e);
        }
    }

    public record ClapNotification(String video){}

}
