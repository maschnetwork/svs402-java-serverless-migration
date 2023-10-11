// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.serverlessflix.claps;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record EventBridgeEvent<T>(
        String version,
        String account,
        String region,
        T detail,
        @JsonProperty(value = "detail-type") String detailType,
        String source,
        String id,
        LocalDateTime time,
        List<String> resources) {
}
