package com.kynn.reevo_backend.video.api.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.kynn.reevo_backend.video.internal.domain.VideoPrivacy;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;

public record VideoUploadRequest(
        String description,
        VideoPrivacy videoPrivacy,
        LocalDateTime scheduleAt,
        Boolean allowComment,
        @NotBlank
        MultipartFile videoFile
) {}