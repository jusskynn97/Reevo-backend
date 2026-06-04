
package com.kynn.reevo_backend.watchtogether.api;

import java.util.UUID;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.kynn.reevo_backend.watchtogether.api.dto.ChangeVideoRequest;
import com.kynn.reevo_backend.watchtogether.api.dto.VideoSyncEvent;
import com.kynn.reevo_backend.watchtogether.internal.service.WatchTogetherService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WatchTogetherFacade {

    private final WatchTogetherService watchTogetherService;

    @MessageMapping("/room/{roomId}/video-sync")
    public void syncVideo(@DestinationVariable UUID roomId, @Payload String rawPayload) {
        log.info("WatchTogetherFacade.syncVideo called! RoomId: {}, Raw payload: {}", roomId, rawPayload);
        try {
            VideoSyncEvent event = new com.fasterxml.jackson.databind.ObjectMapper().readValue(rawPayload, VideoSyncEvent.class);
            log.info("Parsed VideoSyncEvent successfully: {}", event);
            // Set userId manually
            watchTogetherService.syncVideo(roomId, UUID.fromString("00000000-0000-0000-0000-000000000000"), event);
        } catch (Exception e) {
            log.error("Error parsing VideoSyncEvent", e);
        }
    }

    @MessageMapping("/room/{roomId}/video-change")
    public void changeVideo(@DestinationVariable UUID roomId, @Payload ChangeVideoRequest request) {
        log.info("WatchTogetherFacade.changeVideo called! RoomId: {}, Request: {}", roomId, request);
        watchTogetherService.changeVideo(roomId, UUID.fromString("00000000-0000-0000-0000-000000000000"), request.getVideoId(), request.getVideoUrl(), request.getThumbnailUrl());
    }
}
