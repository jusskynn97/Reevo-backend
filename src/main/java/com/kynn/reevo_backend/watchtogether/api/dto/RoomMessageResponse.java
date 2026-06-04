
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
public class RoomMessageResponse {
    private UUID id;
    private UUID senderId;
    private String senderUsername;
    private String senderAvatarUrl;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
}

