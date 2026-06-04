package com.kynn.reevo_backend.feed.internal.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kynn.reevo_backend.feed.internal.domain.UserVideoEvent;
import com.kynn.reevo_backend.feed.internal.domain.UserVideoEventType;
import com.kynn.reevo_backend.feed.internal.repository.UserVideoEventRepository;
import com.kynn.reevo_backend.tag.internal.service.UserTagPreferenceService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedEventService {

    private final UserVideoEventRepository userVideoEventRepository;
    private final UserTagPreferenceService userTagPreferenceService;

    public void recordImpression(UUID userId, UUID videoId) {
        userVideoEventRepository.save(UserVideoEvent.builder()
                .userId(userId)
                .videoId(videoId)
                .eventType(UserVideoEventType.IMPRESSION)
                .build());
    }

    public void recordWatch(UUID userId, UUID videoId, long watchMs) {
        userVideoEventRepository.save(UserVideoEvent.builder()
                .userId(userId)
                .videoId(videoId)
                .eventType(UserVideoEventType.WATCH)
                .watchMs(watchMs)
                .build());
        userTagPreferenceService.updateFromWatch(userId, videoId, watchMs);
    }

    public void recordLike(UUID userId, UUID videoId) {
        userVideoEventRepository.save(UserVideoEvent.builder()
                .userId(userId)
                .videoId(videoId)
                .eventType(UserVideoEventType.LIKE)
                .build());
        userTagPreferenceService.updateFromLike(userId, videoId, 1.0);
    }

    public void recordUnlike(UUID userId, UUID videoId) {
        userVideoEventRepository.save(UserVideoEvent.builder()
                .userId(userId)
                .videoId(videoId)
                .eventType(UserVideoEventType.UNLIKE)
                .build());
    }

    public void recordComment(UUID userId, UUID videoId) {
        userVideoEventRepository.save(UserVideoEvent.builder()
                .userId(userId)
                .videoId(videoId)
                .eventType(UserVideoEventType.COMMENT)
                .build());
        userTagPreferenceService.updateFromLike(userId, videoId, 0.3);
    }
}
