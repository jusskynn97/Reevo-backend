package com.kynn.reevo_backend.video.internal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AIDetectionConfig {
    
    @Bean
    public WebClient aiDetectionWebClient() {
        return WebClient.create("https://3000-01ksym88sja513w7bnt0tq58ch.cloudspaces.litng.ai");
    }
}
