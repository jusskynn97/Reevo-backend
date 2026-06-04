package com.kynn.reevo_backend.interaction.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class VideoCommentUpdatedEvent {
    private final UUID videoId;
    private final int commentCount;
    private final UUID userId;
}
