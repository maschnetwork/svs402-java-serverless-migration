// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.serverlessflix.claps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.serverlessflix.claps.domain.Video;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ClapsService {

    private static final Logger logger = LoggerFactory.getLogger(ClapsService.class);

    private final String tableName;
    private final DynamoDbAsyncClient dynamoDbAsyncClient;

    public ClapsService(String tableName) {
        this(tableName, DynamoDbAsyncClient.create());
    }

    public ClapsService(String tableName, DynamoDbAsyncClient dynamoDbAsyncClient) {
        this.tableName = tableName;
        this.dynamoDbAsyncClient = dynamoDbAsyncClient;
    }

    public void createVideo(Video video) throws UnableToSaveException {
        Map<String, AttributeValue> attributeMap = Map.of("id", AttributeValue.fromS(video.id()),
                "title", AttributeValue.fromS("fake title"),
                "author", AttributeValue.fromS(video.author().username()),
                "claps", AttributeValue.fromN("0"));

        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(this.tableName)
                .item(attributeMap)
                .build();

        try {
            this.dynamoDbAsyncClient.putItem(putItemRequest).get();
            logger.info("Video with ID: {} created", video.id());
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
            throw new UnableToSaveException();
        }
    }
}
