package com.kynn.reevo_backend.feed.internal.client;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.kynn.reevo_backend.feed.internal.client.dto.RecoRankRequest;
import com.kynn.reevo_backend.feed.internal.client.dto.RecoRankResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FeedRecommendationClient {

    private final boolean enabled;
    private final RestClient restClient;

    public FeedRecommendationClient(
            @Value("${feed.reco.enabled:false}") boolean enabled,
            @Value("${feed.reco.base-url:http://localhost:8001}") String baseUrl,
            RestClient.Builder restClientBuilder) {
        this.enabled = enabled;
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public List<UUID> rank(RecoRankRequest request) {
        if (!enabled) {
            return null;
        }

        try {
            RecoRankResponse response = restClient.post()
                    .uri("/rank")
                    .body(request)
                    .retrieve()
                    .body(RecoRankResponse.class);

            return response == null ? null : response.rankedVideoIds();
        } catch (Exception e) {
            log.warn("Feed reco service call failed: {}", e.getMessage());
            return null;
        }
    }
}

