package com.kynn.reevo_backend.video.api.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;

public record VideoUploadRequest(
        @NotBlank 
        String title,
        String description,
        List<String> hashtags,
        @NotBlank 
        MultipartFile videoFile
) {}