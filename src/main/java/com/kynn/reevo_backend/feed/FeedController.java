package com.kynn.reevo_backend.feed;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kynn.reevo_backend.common.api.ApiResponse;
import com.kynn.reevo_backend.common.api.CurrentUserId;
import com.kynn.reevo_backend.feed.api.FeedFacade;
import com.kynn.reevo_backend.feed.api.dto.FeedResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedFacade feedFacade;

    @GetMapping("/videos")
    public ApiResponse<FeedResponse> getFeed(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size,
            @CurrentUserId UUID currentUserId) {

        LocalDateTime cursorTime = null;
        if (cursor != null && !cursor.isBlank()) {
            cursorTime = LocalDateTime.parse(cursor);
        }

        return ApiResponse.ok(feedFacade.getVideoFeed(cursorTime, size, currentUserId));
    }
}

