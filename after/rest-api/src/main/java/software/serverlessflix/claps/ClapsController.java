// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.serverlessflix.claps;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/claps")
public class ClapsController {

    private final ClapsService clapsService;

    public ClapsController(ClapsService clapsService) {
        this.clapsService = clapsService;
    }

    /* public ClapsResponse claps(String videoId) {
        int claps = clapsService.getClaps(videoId);
    } */

}
