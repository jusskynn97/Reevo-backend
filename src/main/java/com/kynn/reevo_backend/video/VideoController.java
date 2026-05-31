package com.kynn.reevo_backend.video;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.kynn.reevo_backend.common.api.ApiResponse;
import com.kynn.reevo_backend.common.api.CurrentUserId;
import com.kynn.reevo_backend.video.api.VideoFacade;
import com.kynn.reevo_backend.video.api.dto.VideoItemResponse;
import com.kynn.reevo_backend.video.api.dto.VideoUploadRequest;
import com.kynn.reevo_backend.video.api.dto.VideoUploadResponse;
import com.kynn.reevo_backend.video.internal.service.UploadProgressService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoFacade videoFacade;
    private final UploadProgressService uploadProgressService;

    @GetMapping(value = "/videos/{videoId}/progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamUploadProgress(@PathVariable UUID videoId) {
        return uploadProgressService.createEmitter(videoId);
    }

    @PostMapping("/upload")
    public ApiResponse<VideoUploadResponse> upload(
            @ModelAttribute VideoUploadRequest request,
            @CurrentUserId UUID userId) throws IOException {
        VideoUploadResponse response = videoFacade.uploadVideo(request, userId);
        return ApiResponse.ok(response);
    }

    private void validateVideoFile(MultipartFile file) throws BadRequestException {
        if (file.isEmpty()) {
            throw new BadRequestException("Video file is required");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new BadRequestException("Only video files are allowed");
        }
        if (file.getSize() > 500 * 1024 * 1024) { // 500MB
            throw new BadRequestException("Video size must be less than 500MB");
        }
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<VideoItemResponse>> getUserVideos(
            @PathVariable UUID userId,
            @CurrentUserId UUID currentUserId) {
        log.info("getUserVideos called with userId: {}, currentUserId: {}", userId, currentUserId);
        List<VideoItemResponse> videos = videoFacade.getUserVideos(userId, currentUserId);
        log.info("Found {} videos for user {}", videos.size(), userId);
        return ApiResponse.ok(videos);
    }

    @GetMapping("/test")
    public ApiResponse<String> test(@CurrentUserId UUID userId) throws IOException {
        return ApiResponse.ok(userId.toString());
    }
}
