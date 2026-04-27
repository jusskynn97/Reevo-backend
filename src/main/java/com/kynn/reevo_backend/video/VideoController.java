package com.kynn.reevo_backend.video;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @AuthenticationPrincipal UUID userId) throws IOException {  

        VideoUploadResponse response = videoFacade.uploadVideo(request, userId);
        return ApiResponse.ok(response);
    }
}