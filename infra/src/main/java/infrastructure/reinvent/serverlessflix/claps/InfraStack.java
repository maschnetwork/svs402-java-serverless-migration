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

        Role lambdaRole = Role.Builder.create(this, "Role")
                .roleName("invokeNotifierFunctionRole")
                .assumedBy(new ServicePrincipal("scheduler.amazonaws.com"))
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
                        "CREATOR_NOTIFICATION_TARGET", "amazing",
                        "CREATOR_NOTIFICATION_ROLE_ARN", lambdaRole.getRoleArn()))
                .build();

        videoClapsTable.grantReadWriteData(newVideoHandler);



        PolicyStatement statement = PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .actions(List.of("lambda:InvokeFunction"))
                .resources(List.of(newVideoHandler.getFunctionArn()))
                .build();
        Policy policy = Policy.Builder.create(this, "Policy")
                .roles(List.of(lambdaRole))
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
//                        .roleArn(lambdaRole.getRoleArn())
//                        .build())
//                .build();

    }

}

