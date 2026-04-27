package com.kynn.reevo_backend.video.api;

import java.util.UUID;

import org.springframework.modulith.NamedInterface;

import com.kynn.reevo_backend.video.api.dto.VideoUploadRequest;
import com.kynn.reevo_backend.video.api.dto.VideoUploadResponse;

@NamedInterface
public interface VideoFacade {
    VideoUploadResponse uploadVideo(VideoUploadRequest request, UUID uploaderId) throws java.io.IOException;
    // additional methods: getVideo, deleteVideo, etc.
}
