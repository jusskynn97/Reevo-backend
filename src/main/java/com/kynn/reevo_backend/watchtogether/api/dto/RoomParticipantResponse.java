
package com.kynn.reevo_backend.watchtogether.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomParticipantResponse {
    private UUID id;
    private UUID userId;
    private String username;
    private String avatarUrl;
    private Boolean isMuted;
    private Boolean isSpeaking;
    private LocalDateTime joinedAt;
}

