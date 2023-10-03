package infrastructure.reinvent.serverlessflix.claps;

import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.Policy;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.SnapStartConf;
import software.amazon.awscdk.services.lambda.Tracing;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.lambda.Runtime;

import java.util.List;
import java.util.Map;

public class InfraStack extends Stack {

    public InfraStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InfraStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Table videoClapsTable = Table.Builder.create(this, "VideoClaps")
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .partitionKey(Attribute.builder()
                        .name("id")
                        .type(AttributeType.STRING)
                        .build())
                .build();

        Role invokeNotifierFunctionRole = Role.Builder.create(this, "Role")
                .roleName("invokeNotifierFunctionRole")
                .assumedBy(new ServicePrincipal("scheduler.amazonaws.com"))
                .build();

        Function notifyCreatorHandler = Function.Builder.create(this,"NotifyCreatorHandler")
                .runtime(Runtime.JAVA_17)
                .memorySize(2048)
                .handler("software.serverlessflix.claps.NotifyCreator")
                .timeout(Duration.seconds(30))
                .snapStart(SnapStartConf.ON_PUBLISHED_VERSIONS)
                .code(Code.fromAsset("../after/scheduled-job/target/svs402-scheduled-job-1.0.jar"))
                .tracing(Tracing.ACTIVE)
                .environment(Map.of(
                        "TABLE_NAME", videoClapsTable.getTableName()))
                .build();

        Function newVideoHandler = Function.Builder.create(this,"NewVideoHandler")
                .runtime(Runtime.JAVA_17)
                .memorySize(2048)
                .handler("software.serverlessflix.claps.NewVideoHandler")
                .timeout(Duration.seconds(30))
                .snapStart(SnapStartConf.ON_PUBLISHED_VERSIONS)
                .code(Code.fromAsset("../after/new-video-function/target/svs402-new-video-function-1.0.jar"))
                .tracing(Tracing.ACTIVE)
                .environment(Map.of(
                        "TABLE_NAME", videoClapsTable.getTableName(),
                        // @TODO use alias
                        "CREATOR_NOTIFICATION_TARGET", notifyCreatorHandler.getFunctionArn(),
                        "CREATOR_NOTIFICATION_ROLE_ARN", invokeNotifierFunctionRole.getRoleArn()))
                .build();

        videoClapsTable.grantReadWriteData(newVideoHandler);
        newVideoHandler.addToRolePolicy(PolicyStatement.Builder.create()
                .actions(List.of("scheduler:CreateSchedule"))
                .resources(List.of("*"))
                .build());
        // required to pass a role to another service - https://docs.aws.amazon.com/ARG/latest/userguide/security_iam_troubleshoot.html#security_troubleshoot-passrole
        newVideoHandler.addToRolePolicy(PolicyStatement.Builder.create()
                .actions(List.of("iam:PassRole"))
                .resources(List.of(invokeNotifierFunctionRole.getRoleArn()))
                .build());

        PolicyStatement statement = PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .actions(List.of("lambda:InvokeFunction"))
                .resources(List.of(newVideoHandler.getFunctionArn()))
                .build();
        Policy policy = Policy.Builder.create(this, "Policy")
                .roles(List.of(invokeNotifierFunctionRole))
                .policyName("ScheduleToInvokeLambdas")
                .statements(List.of(statement))
                .build();

//        CfnScheduleGroup scheduleGroup = CfnScheduleGroup.Builder.create(this, "scheduleGroup")
//                .name("lambdaSchedules")
//                .build();
//
//        CfnSchedule.Builder.create(this, "lambdaSchedule")
//                .flexibleTimeWindow(CfnSchedule.FlexibleTimeWindowProperty.builder()
//                        .mode("OFF").build())
//                .groupName(scheduleGroup.getName())
//                .scheduleExpression("rate(1 minute)")
//                .target(CfnSchedule.TargetProperty.builder()
//                        .arn(newVideoHandler.getFunctionArn())
//                        .roleArn(invokeNotifierFunctionRole.getRoleArn())
//                        .build())
//                .build();

    }

}

