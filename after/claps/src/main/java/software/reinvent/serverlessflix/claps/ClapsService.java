// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.reinvent.serverlessflix.claps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.reinvent.serverlessflix.claps.domain.Video;

import java.util.Map;

public class ClapsService {

    private static final Logger logger = LoggerFactory.getLogger(ClapsService.class);

    private final DynamoDbClient dynamoDbClient = DynamoDbClient.create();
    private final String tableName;

    public ClapsService(String tableName) {
        this.tableName = tableName;
    }

    public void createVideo(Video video) {
        Map<String, AttributeValue> attributeMap = Map.of("id", AttributeValue.fromS(video.id()),
                "title", AttributeValue.fromS("fake title"),
                "author", AttributeValue.fromS(video.author().username()),
                "claps", AttributeValue.fromN("0"));

        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(this.tableName)
                .item(attributeMap)
                .build();
        PutItemResponse putItemResponse = this.dynamoDbClient.putItem(putItemRequest);

        logger.info("Consumed capacity: " + putItemResponse.consumedCapacity().capacityUnits());
    }
}
