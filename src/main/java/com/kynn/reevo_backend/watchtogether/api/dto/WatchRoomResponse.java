
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
public class WatchRoomResponse {
    private UUID id;
    private UUID creatorId;
    private String name;
    private String description;
    private String videoId;
    private String videoUrl;
    private String thumbnailUrl;
    private String privacy;
    private Boolean isActive;
    private Integer maxUsers;
    private Integer participantCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

