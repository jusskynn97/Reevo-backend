package com.kynn.reevo_backend.feed;

import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kynn.reevo_backend.common.api.ApiResponse;
import com.kynn.reevo_backend.common.api.CurrentUserId;
import com.kynn.reevo_backend.feed.api.dto.VideoImpressionRequest;
import com.kynn.reevo_backend.feed.api.dto.VideoWatchRequest;
import com.kynn.reevo_backend.feed.internal.service.FeedEventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/feed/events")
@RequiredArgsConstructor
public class FeedEventController {

    private final FeedEventService feedEventService;

    @PostMapping("/impression")
    public ApiResponse<Void> impression(@RequestBody VideoImpressionRequest request, @CurrentUserId UUID userId) {
        feedEventService.recordImpression(userId, request.videoId());
        return ApiResponse.ok(null);
    }

    @PostMapping("/watch")
    public ApiResponse<Void> watch(@RequestBody VideoWatchRequest request, @CurrentUserId UUID userId) {
        long watchMs = request.watchMs() == null ? 0 : request.watchMs();
        if (watchMs > 0) {
            feedEventService.recordWatch(userId, request.videoId(), watchMs);
        }
        return ApiResponse.ok(null);
    }
}

