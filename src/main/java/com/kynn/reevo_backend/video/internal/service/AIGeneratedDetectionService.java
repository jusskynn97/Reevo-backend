package com.kynn.reevo_backend.video.internal.service;

import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.kynn.reevo_backend.video.api.dto.AIGeneratedDetectionResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIGeneratedDetectionService {
    
    private final WebClient aiDetectionWebClient;
    
    public boolean detectIfVideoIsAIGenerated(byte[] videoBytes, String filename) {
        try {
            log.info("Calling AI detection service for file: {}", filename);
            
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", videoBytes)
                   .filename(filename)
                   .contentType(MediaType.parseMediaType("video/mp4"));
            
            AIGeneratedDetectionResponse response = aiDetectionWebClient.post()
                .uri("/predict")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(AIGeneratedDetectionResponse.class)
                .block();
                
            if (response != null) {
                log.info("AI detection result for {}: isAiGenerated={}, confidence={}", 
                    filename, response.isAiGenerated(), response.getConfidencePct());
                return response.isAiGenerated();
            }
            
            return false;
            
        } catch (WebClientResponseException e) {
            log.error("Error calling AI detection API: status={}, response={}", 
                e.getStatusCode(), e.getResponseBodyAsString(), e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error during AI detection", e);
            return false;
        }
    }
}
