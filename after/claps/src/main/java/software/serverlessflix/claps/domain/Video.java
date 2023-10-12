// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.serverlessflix.claps.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public record Video(String createdAt,
                    String thumbnail,
                    String playbackUrl,
                    String channel,
                    String title,
                    String id,
                    Author author) {

    public Map<String, AttributeValue> toDynamoDBAttributeMap() {
        return Map.of("id", AttributeValue.fromS(this.id()),
                "title", AttributeValue.fromS(this.title()),
                "playbackUrl", AttributeValue.fromS(this.playbackUrl()),
                "channel", AttributeValue.fromS(this.channel()),
                "thumbnail", AttributeValue.fromS(this.channel()),
                "createdAt", AttributeValue.fromS(this.createdAt()),
                "author", AttributeValue.fromS(this.author().username()),
                "author_email", AttributeValue.fromS(this.author().email()));
    }

    public static Video fromDynamoDBAttributeMap(Map<String, AttributeValue> dynamoAttributeMap){
        return new Video(dynamoAttributeMap.getOrDefault("createdAt", AttributeValue.fromS("")).toString(),
                dynamoAttributeMap.getOrDefault("thumbnail", AttributeValue.fromS("")).toString(),
                dynamoAttributeMap.getOrDefault("playbackUrl", AttributeValue.fromS("")).toString(),
                dynamoAttributeMap.getOrDefault("channel", AttributeValue.fromS("")).toString(),
                dynamoAttributeMap.getOrDefault("title", AttributeValue.fromS("")).toString(),
                dynamoAttributeMap.getOrDefault("id", AttributeValue.fromS("")).toString(),
                new Author(dynamoAttributeMap.getOrDefault("author", AttributeValue.fromS("")).toString(),
                        dynamoAttributeMap.getOrDefault("author_email", AttributeValue.fromS("")).toString())
                );
    }

}
