package infrastructure.reinvent.serverlessflix.claps;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.events.EventBus;
import software.amazon.awscdk.services.events.EventPattern;
import software.amazon.awscdk.services.events.Rule;
import software.amazon.awscdk.services.events.targets.LambdaFunction;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.Policy;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.SnapStartConf;
import software.amazon.awscdk.services.lambda.Tracing;
import software.amazon.awscdk.services.lambda.eventsources.SqsEventSource;
import software.amazon.awscdk.services.scheduler.CfnSchedule;
import software.amazon.awscdk.services.scheduler.CfnScheduleGroup;
import software.amazon.awscdk.services.sns.subscriptions.SqsSubscription;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;
import software.amazon.awscdk.services.lambda.Runtime;

import java.util.List;
import java.util.Map;

public class InfraStack extends Stack {

    public InfraStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public InfraStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        var newVideoEventBus = EventBus.Builder.create(this, "NewVideoEventBus")
                    .eventBusName("videos").build();

        var videoClapsTable = Table.Builder.create(this, "VideoClaps")
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .partitionKey(Attribute.builder()
                        .name("id")
                        .type(AttributeType.STRING)
                        .build())
                .build();

        var invokeNotifierFunctionRole = Role.Builder.create(this, "Role")
                .roleName("invokeNotifierFunctionRole")
                .assumedBy(new ServicePrincipal("scheduler.amazonaws.com"))
                .build();

        var notifyCreatorHandler = Function.Builder.create(this,"NotifyCreatorHandler")
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

        var scheduleGroup = CfnScheduleGroup.Builder.create(this, "scheduleGroup")
                .name("ClapNotifications")
                .build();

        var newVideoHandler = Function.Builder.create(this,"NewVideoHandler")
                .runtime(Runtime.JAVA_17)
                .memorySize(2048)
                .handler("software.serverlessflix.claps.NewVideoHandler")
                .timeout(Duration.seconds(30))
                .snapStart(SnapStartConf.ON_PUBLISHED_VERSIONS)
                .code(Code.fromAsset("../after/new-video-function/target/svs402-new-video-function-1.0.jar"))
                .tracing(Tracing.ACTIVE)
                .environment(Map.of(
                        "TABLE_NAME", videoClapsTable.getTableName(),
                         "CREATOR_NOTIFICATION_TARGET", notifyCreatorHandler.getFunctionArn(),
                        "CREATOR_NOTIFICATION_ROLE_ARN", invokeNotifierFunctionRole.getRoleArn(),
                        "SCHEDULING_GROUP", scheduleGroup.getName()))
                .build();

        videoClapsTable.grantReadWriteData(newVideoHandler);

        var target = LambdaFunction.Builder.create(newVideoHandler).build();
        var eventPattern = EventPattern.builder().source(List.of("software.serverlessflix.video")).build();
        var myRule = Rule.Builder.create(this, "NewVideoRule")
                .targets(List.of(target))
                .eventBus(newVideoEventBus)
                .eventPattern(eventPattern)
                .build();



        newVideoHandler.addToRolePolicy(PolicyStatement.Builder.create()
                .actions(List.of("scheduler:CreateSchedule"))
                .resources(List.of("*"))
                .build());
        // required to pass a role to another service - https://docs.aws.amazon.com/ARG/latest/userguide/security_iam_troubleshoot.html#security_troubleshoot-passrole
        newVideoHandler.addToRolePolicy(PolicyStatement.Builder.create()
                .actions(List.of("iam:PassRole"))
                .resources(List.of(invokeNotifierFunctionRole.getRoleArn()))
                .build());

        var statement = PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .actions(List.of("lambda:InvokeFunction"))
                .resources(List.of(notifyCreatorHandler.getFunctionArn()))
                .build();
        var policy = Policy.Builder.create(this, "Policy")
                .roles(List.of(invokeNotifierFunctionRole))
                .policyName("ScheduleToInvokeLambdas")
                .statements(List.of(statement))
                .build();

        var clapsRestApiFunction = Function.Builder.create(this, "VideoRestApiFunction")
                .runtime(Runtime.JAVA_17)
                .functionName("video-spring-rest-api")
                .memorySize(2048)
                //Todo: Enable SnapStart
                .timeout(Duration.seconds(29))
                .code(Code.fromAsset("../after/rest-api/target/svs402-rest-api-1.0.jar"))
                .handler("com.amazonaws.serverless.proxy.spring.SpringDelegatingLambdaContainerHandler")
                .environment(Map.of(
                        "MAIN_CLASS", "software.serverlessflix.claps.ClapsWebApp",
                        "TABLE_NAME", videoClapsTable.getTableName()
                ))
                .build();

        var api = RestApi.Builder.create(this, "VideoApi").restApiName("VideoApi").build();
        var integration = LambdaIntegration.Builder.create(clapsRestApiFunction).build();
        var proxyResourceOption = ProxyResourceOptions.builder().defaultIntegration(integration).build();
        api.getRoot().addProxy(proxyResourceOption);

        var sqsQueue = Queue.Builder.create(this, "ClapQueue").queueName("ClapQueue").build();




        var sqsRole = Role.Builder.create(this, "ApiGatewaySqsRole")
                .roleName("ClapApiGatewaySqsRole")
                .assumedBy(new ServicePrincipal("apigateway.amazonaws.com"))
                .build();

        sqsQueue.grantSendMessages(sqsRole);
        var integrationOptions = IntegrationOptions.builder()
                .integrationResponses(List.of(IntegrationResponse.builder().statusCode("200").build()))
                .requestTemplates(Map.of( "application/json", "Action=SendMessage&MessageBody=$input.body"))
                .requestParameters(Map.of("integration.request.header.Content-Type", "'application/x-www-form-urlencoded'"))
                .passthroughBehavior(PassthroughBehavior.NEVER)
                .credentialsRole(sqsRole)
                .build();
        videoClapsTable.grantReadData(clapsRestApiFunction);

        var sqsIntegration = AwsIntegration.Builder.create()
                .integrationHttpMethod("POST")
                .service("sqs")
                .options(integrationOptions)
                .path("%s/%s".formatted(this.getAccount(), sqsQueue.getQueueName()))
                .build();

        api.getRoot().addResource("claps")
                .addMethod("POST", sqsIntegration)
                .addMethodResponse(MethodResponse.builder().statusCode("200").build());

        var sqsEventSource = SqsEventSource.Builder
                        .create(sqsQueue)
                        .batchSize(25)
                        .maxBatchingWindow(Duration.seconds(1))
                        .build();

        var clapProcessor = Function.Builder.create(this,"ClapProcessor")
                .runtime(Runtime.JAVA_17)
                .memorySize(1024)
                .handler("software.serverlessflix.claps.ClapProcessor")
                .timeout(Duration.seconds(30))
                .snapStart(SnapStartConf.ON_PUBLISHED_VERSIONS)
                .code(Code.fromAsset("../after/clap-processor/target/svs402-clap-processor-1.0.jar"))
                .tracing(Tracing.ACTIVE)
                .environment(Map.of(
                        "TABLE_NAME", videoClapsTable.getTableName())
                )
                .events(List.of(sqsEventSource))
                .build();

        sqsQueue.grantConsumeMessages(clapProcessor);
        videoClapsTable.grantFullAccess(clapProcessor);

        CfnOutput.Builder.create(this, "ApiEndpointSpring")
                .value(api.getUrl())
                .build();
    }

}

