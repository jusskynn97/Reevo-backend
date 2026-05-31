package com.kynn.reevo_backend.feed.api.dto;

import java.util.List;

import com.kynn.reevo_backend.video.api.dto.VideoItemResponse;

public record FeedResponse(
        List<VideoItemResponse> items,
        String nextCursor
) {}

