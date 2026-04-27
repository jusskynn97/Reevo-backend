package com.kynn.reevo_backend.video.api.dto;

import java.util.UUID;

public record VideoUploadResponse(
        UUID uploadId,           // video id
        String message,
        String status
) {}
