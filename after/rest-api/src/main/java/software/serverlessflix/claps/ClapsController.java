// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.serverlessflix.claps;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.serverlessflix.claps.domain.Video;

import java.util.List;

@RestController
public class ClapsController {

    private final ClapsService clapsService;

    public ClapsController(ClapsService clapsService) {
        this.clapsService = clapsService;
    }

    @GetMapping("/videos")
    public ResponseEntity<List<Video>> getVideos() {
        return ResponseEntity.ok(clapsService.getVideos());
    }

    @GetMapping("/videos/{videoId}")
    public ResponseEntity<Video> getVideo(@PathVariable String videoId) {
        try {
            return ResponseEntity.ok(clapsService.getVideo(videoId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
