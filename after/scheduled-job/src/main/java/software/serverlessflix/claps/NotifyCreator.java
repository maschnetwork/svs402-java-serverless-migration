// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.serverlessflix.claps;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class NotifyCreator implements RequestHandler<EventBridgeSchedulerEvent, Void> {

    @Override
    public Void handleRequest(EventBridgeSchedulerEvent input, Context context) {


        return null;
    }
}
