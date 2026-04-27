package com.kynn.reevo_backend.video.internal.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kynn.reevo_backend.video.api.VideoFacade;
import com.kynn.reevo_backend.video.api.dto.VideoUploadRequest;
import com.kynn.reevo_backend.video.api.dto.VideoUploadResponse;
import com.kynn.reevo_backend.video.internal.domain.Video;
import com.kynn.reevo_backend.video.internal.domain.VideoStatus;
import com.kynn.reevo_backend.video.internal.repository.VideoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoService implements VideoFacade {

    private final VideoRepository videoRepository;
    private final CloudinaryUploadService cloudinaryUploadService;

    @Override
    public VideoUploadResponse uploadVideo(VideoUploadRequest req, UUID uploaderId) throws BadRequestException, IOException {
        // Validate file
        if (req.videoFile().isEmpty() || !isVideoFile(req.videoFile())) {
            throw new BadRequestException("Invalid video file");
        }

        Video video = new Video();
        video.setUploaderId(uploaderId);
        video.setTitle(req.title());
        video.setDescription(req.description());
        video.setHashtags(String.join(",", req.hashtags() != null ? req.hashtags() : List.of()));
        video.setStatus(VideoStatus.PENDING);

        video = videoRepository.save(video);
        UUID videoId = video.getId();

        // Đọc file bytes trong context của request (trước khi file tạm thời bị xóa)
        byte[] fileBytes = req.videoFile().getBytes();
        
        // Bắt đầu upload bất đồng bộ với file bytes thay vì MultipartFile
        cloudinaryUploadService.uploadAsync(videoId, fileBytes);

        return new VideoUploadResponse(
                videoId,
                "Video is being uploaded. You will be notified when it's ready.",
                "PENDING"
        );
    }

    private boolean isVideoFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("video/");
    }
}
