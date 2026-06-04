package com.kynn.reevo_backend.interaction.api.dto;

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
public class CommentResponse {
    private UUID id;
    private UUID videoId;
    private UUID userId;
    private String username;
    private String avatarUrl;
    private String content;
    private UUID parentId;
    private LocalDateTime createdAt;
    private long likeCount;
}
