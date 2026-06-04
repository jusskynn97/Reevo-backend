package com.kynn.reevo_backend.interaction.api;

import org.springframework.modulith.NamedInterface;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@NamedInterface
public interface InteractionFacade {
    int getLikeCount(UUID videoId);
    int getCommentCount(UUID videoId);
    boolean isVideoLikedByUser(UUID videoId, UUID userId);

    Map<UUID, Integer> getLikeCounts(List<UUID> videoIds);
    Map<UUID, Integer> getCommentCounts(List<UUID> videoIds);
    Set<UUID> getLikedVideoIds(UUID userId, List<UUID> videoIds);
}
