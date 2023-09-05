package com.myorg;

import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.Policy;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.scheduler.CfnSchedule;
import software.amazon.awscdk.services.scheduler.CfnScheduleGroup;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.lambda.Runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfraStack extends Stack {

    public InfraStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InfraStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Function lambdaFn = Function.Builder.create(this,"ScheduledFunction")
                .runtime(Runtime.JAVA_17)
                .memorySize(2048)
                .handler("helloworld.ScheduledJob")
                .timeout(Duration.seconds(30))
                .code(Code.fromAsset("../after/scheduled-job/target/svs402-scheduled-job-1.0.jar"))
                .build();

        Role lambdaRole = Role.Builder.create(this, "Role")
                .assumedBy(new ServicePrincipal("scheduler.amazonaws.com"))
                .build();
        List<Role> roles = new ArrayList<>();
        roles.add(lambdaRole);

        PolicyStatement statement = PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .actions(Arrays.asList("lambda:InvokeFunction"))
                .resources(Arrays.asList(lambdaFn.getFunctionArn()))
                .build();
        Policy policy = Policy.Builder.create(this, "Policy")
                .roles(roles)
                .policyName("ScheduleToInvokeLambdas")
                .statements(Arrays.asList(statement))
                .build();

        CfnScheduleGroup scheduleGroup = CfnScheduleGroup.Builder.create(this, "scheduleGroup")
                .name("lambdaSchedules")
                .build();

        CfnSchedule.Builder.create(this, "lambdaSchedule")
                .flexibleTimeWindow(CfnSchedule.FlexibleTimeWindowProperty.builder()
                        .mode("OFF").build())
                .groupName(scheduleGroup.getName())
                .scheduleExpression("rate(1 minute)")
                .target(CfnSchedule.TargetProperty.builder()
                        .arn(lambdaFn.getFunctionArn())
                        .roleArn(lambdaRole.getRoleArn())
                        .build())
                .build();

    }

}

