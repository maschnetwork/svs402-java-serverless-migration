// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.serverlessflix.claps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.serverlessflix.claps.domain.Video;

import java.util.List;
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
        var putItemRequest= PutItemRequest.builder()
                .tableName(this.tableName)
                .item(video.toDynamoDBAttributeMap())
                .build();

        try {
            this.dynamoDbAsyncClient.putItem(putItemRequest).get();
            logger.info("Video with ID: {} created", video.id());
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
            throw new UnableToSaveException(e);
        }
    }

    public List<Video> getVideos()  {
        var scanRequest= ScanRequest.builder()
                .tableName(this.tableName)
                .limit(50) //Returning only 50 videos - For prod use pagination
                .build();

        try {
            var response = this.dynamoDbAsyncClient.scan(scanRequest).get();
            return response.items()
                    .stream()
                    .map(Video::fromDynamoDBAttributeMap)
                    .toList();
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Unable to query DynamoDB table", e);
        }
    }

    public Video getVideo(String videoId)  {
        var getItemRequest= GetItemRequest.builder()
                .tableName(this.tableName)
                .key(Map.of("id", AttributeValue.fromS(videoId)))
                .build();

        try {
            var response = this.dynamoDbAsyncClient.getItem(getItemRequest).get();
            if (response.hasItem()) {
                return Video.fromDynamoDBAttributeMap(response.item());
            } else {
                throw new RuntimeException("Video with id %s not found".formatted(videoId));
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Unable to query DynamoDB table", e);
        }
    }
}
