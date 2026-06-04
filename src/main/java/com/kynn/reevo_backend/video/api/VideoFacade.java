package com.kynn.reevo_backend.video.api;

import java.util.List;
import java.util.UUID;

import com.kynn.reevo_backend.video.api.dto.VideoItemResponse;
import org.springframework.modulith.NamedInterface;

import com.kynn.reevo_backend.video.api.dto.VideoUploadRequest;
import com.kynn.reevo_backend.video.api.dto.VideoUploadResponse;

@NamedInterface
public interface VideoFacade {
    VideoUploadResponse uploadVideo(VideoUploadRequest request, UUID uploaderId) throws java.io.IOException;
    boolean existsById(UUID videoId);
    List<VideoItemResponse> getUserVideos(UUID userId, UUID currentUserId);
}
