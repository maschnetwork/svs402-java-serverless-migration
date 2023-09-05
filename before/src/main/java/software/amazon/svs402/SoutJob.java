// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazon.svs402;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class SoutJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        System.out.println("FireInstanceId: " + jobExecutionContext.getFireInstanceId());
    }
}
