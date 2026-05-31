package com.kynn.reevo_backend.tag.internal.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.kynn.reevo_backend.tag.internal.client.dto.TaggingRequest;
import com.kynn.reevo_backend.tag.internal.client.dto.TaggingResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class VideoTaggingClient {

    private final boolean enabled;
    private final RestClient restClient;

    public VideoTaggingClient(
            @Value("${tag.ai.enabled:false}") boolean enabled,
            @Value("${tag.ai.base-url:http://localhost:8001}") String baseUrl,
            RestClient.Builder restClientBuilder) {
        this.enabled = enabled;
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public TaggingResponse tag(TaggingRequest request) {
        if (!enabled) {
            return null;
        }
        try {
            return restClient.post()
                    .uri("/tag")
                    .body(request)
                    .retrieve()
                    .body(TaggingResponse.class);
        } catch (Exception e) {
            log.warn("Video tagging call failed: {}", e.getMessage());
            return null;
        }
    }
}

