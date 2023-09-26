// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.reinvent.serverlessflix.claps.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Video(String createdAt,
                    @JsonProperty("durationmillis")
                    Integer durationMillis,
                    String thumbnail,
                    String playbackUrl,
                    String channel,
                    String id,
                    Author author) {

}
