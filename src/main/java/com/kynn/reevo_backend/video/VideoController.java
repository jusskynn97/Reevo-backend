package com.kynn.reevo_backend.video;

import java.io.IOException;
import java.util.UUID;

import com.kynn.reevo_backend.common.api.CurrentUserId;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.kynn.reevo_backend.common.api.ApiResponse;
import com.kynn.reevo_backend.video.api.VideoFacade;
import com.kynn.reevo_backend.video.api.dto.VideoUploadRequest;
import com.kynn.reevo_backend.video.api.dto.VideoUploadResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoFacade videoFacade;

    @PostMapping("/upload")
    public ApiResponse<VideoUploadResponse> upload(
            @ModelAttribute VideoUploadRequest request,
            @CurrentUserId UUID userId) throws IOException {

        VideoUploadResponse response = videoFacade.uploadVideo(request, userId);
        return ApiResponse.ok(response);
    }

    @GetMapping("/test")
    public ApiResponse<String> test(@CurrentUserId UUID userId) throws IOException {
        return ApiResponse.ok(userId.toString());
    }
}