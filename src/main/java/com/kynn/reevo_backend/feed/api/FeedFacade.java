package com.kynn.reevo_backend.feed.api;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.modulith.NamedInterface;

import com.kynn.reevo_backend.feed.api.dto.FeedResponse;

@NamedInterface
public interface FeedFacade {
    FeedResponse getVideoFeed(LocalDateTime cursor, int size, UUID currentUserId);
}

