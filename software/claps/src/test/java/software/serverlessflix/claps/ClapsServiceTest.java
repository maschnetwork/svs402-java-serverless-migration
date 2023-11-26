package software.serverlessflix.claps;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.serverlessflix.claps.domain.Author;
import software.serverlessflix.claps.domain.Video;

import java.net.URI;
import java.util.concurrent.ExecutionException;

@Testcontainers
@DisplayName("DynamoDBUnicornRepository Unit Test")
public class ClapsServiceTest {

    @Container
    private static final GenericContainer DYNAMODB_CONTAINER = new GenericContainer(DockerImageName.parse("amazon/dynamodb-local:2.0.0"))
            .withExposedPorts(8000);
    private static final String TABLE_NAME = "TABLE";

    private static ClapsService clapsService;

    @BeforeAll
    public static void beforeAll() throws ExecutionException, InterruptedException {
        DynamoDbAsyncClient dynamoDbAsyncClient = DynamoDbAsyncClient.builder()
                .region(Region.EU_WEST_1)
                .endpointOverride(URI.create(String.format("http://%s:%d", DYNAMODB_CONTAINER.getHost(), DYNAMODB_CONTAINER.getFirstMappedPort())))
                .httpClient(AwsCrtAsyncHttpClient.create())
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("fakeMyKeyId", "fakeSecretAccessKey")))
                .build();
        clapsService = new ClapsService(TABLE_NAME, dynamoDbAsyncClient);

        CreateTableResponse createTableResponse = dynamoDbAsyncClient.createTable(CreateTableRequest.builder()
                .tableName(TABLE_NAME)
                .keySchema(KeySchemaElement.builder()
                        .attributeName("id")
                        .keyType(KeyType.HASH)
                        .build())
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName("id")
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .build())
                .get();
        createTableResponse.responseMetadata();
    }

    @Test
    public void createVideo() throws UnableToSaveException {
        Video video = new Video("2023-08-30T14:57:51.169Z",
                "https://dmpdx8pmqxo3f.cloudfront.net/media/ivs/v1/832196373597/ERaAhUZnrHJG/2023/8/30/14/57/ovoTjGdG38Mk/media/hls/720p30/thumbnails/thumb0.jpg",
                "https://dmpdx8pmqxo3f.cloudfront.net/media/ivs/v1/832196373597/ERaAhUZnrHJG/2023/8/30/14/57/ovoTjGdG38Mk/media/hls/720p30/output.mp4",
                "08d143f0-00f1-7052-f97a-cbcda39ff077",
                "My fancy title",
                "08d143f0-00f1-7052-f97a-cbcda39ff077",
                new Author("random1", "random1234123@amazon.de"),
                "0");
        clapsService.createVideo(video);
    }
}
