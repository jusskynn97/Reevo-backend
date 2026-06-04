package com.kynn.reevo_backend.interaction.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class VideoLikeUpdatedEvent {
    private final UUID videoId;
    private final int likeCount;
    private final boolean isLiked;
    private final UUID userId;
}
