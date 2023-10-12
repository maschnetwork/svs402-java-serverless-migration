// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.serverlessflix.claps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ClapsWebApp {

    @Bean
    public ClapsService clapsService(){
        return new ClapsService(System.getenv("TABLE_NAME"));
    }
    public static void main(String[] args) {
        SpringApplication.run(ClapsWebApp.class, args);
    }
}
