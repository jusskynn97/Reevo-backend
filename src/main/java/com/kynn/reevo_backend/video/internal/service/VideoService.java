package com.kynn.reevo_backend.video.internal.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kynn.reevo_backend.interaction.api.InteractionFacade;
import com.kynn.reevo_backend.user.api.UserFacade;
import com.kynn.reevo_backend.video.api.VideoFacade;
import com.kynn.reevo_backend.video.api.dto.VideoItemResponse;
import com.kynn.reevo_backend.video.api.dto.VideoUploadRequest;
import com.kynn.reevo_backend.video.api.dto.VideoUploadResponse;
import com.kynn.reevo_backend.video.internal.domain.Video;
import com.kynn.reevo_backend.video.internal.domain.VideoStatus;
import com.kynn.reevo_backend.video.internal.repository.VideoRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class VideoService implements VideoFacade {

    private final VideoRepository videoRepository;
    private final CloudinaryUploadService cloudinaryUploadService;
    private final UserFacade userFacade;
    private final InteractionFacade interactionFacade;

    public VideoService(
            VideoRepository videoRepository,
            CloudinaryUploadService cloudinaryUploadService,
            UserFacade userFacade,
            @Lazy InteractionFacade interactionFacade) {
        this.videoRepository = videoRepository;
        this.cloudinaryUploadService = cloudinaryUploadService;
        this.userFacade = userFacade;
        this.interactionFacade = interactionFacade;
    }


    @Override
    public VideoUploadResponse uploadVideo(VideoUploadRequest req, UUID uploaderId) throws BadRequestException, IOException {
        // Validate file
        if (req.videoFile().isEmpty() || !isVideoFile(req.videoFile())) {
            throw new BadRequestException("Invalid video file");
        }

        if (req.scheduleAt() != null) {
            // scheduledUpload()
            System.out.println(req.scheduleAt());
        }

        Video video = new Video();
        video.setUploaderId(uploaderId);
        video.setDescription(req.description());
        video.setStatus(VideoStatus.PENDING);

        video = videoRepository.save(video);
        UUID videoId = video.getId();

        byte[] fileBytes = req.videoFile().getBytes();
        String filename = req.videoFile().getOriginalFilename();

        cloudinaryUploadService.uploadAsync(videoId, fileBytes, filename);

        return new VideoUploadResponse(
                videoId,
                "Video is being uploaded. You will be notified when it's ready.",
                "PENDING"
        );
    }


    @Override
    public boolean existsById(UUID videoId) {
        return videoRepository.existsById(videoId);
    }

    @Override
    public List<VideoItemResponse> getUserVideos(UUID userId, UUID currentUserId) {
        log.info("getUserVideos in VideoService called with userId: {}", userId);
        List<Video> videos = videoRepository.findByUploaderIdAndStatusOrderByUploadedAtDesc(userId, VideoStatus.READY);
        log.info("Found {} Video entities in DB for userId: {}", videos.size(), userId);

        List<UUID> videoIds = videos.stream().map(Video::getId).toList();
        Map<UUID, Integer> likeCounts = interactionFacade.getLikeCounts(videoIds);
        Map<UUID, Integer> commentCounts = interactionFacade.getCommentCounts(videoIds);
        Set<UUID> likedIds = currentUserId == null ? Set.of() : interactionFacade.getLikedVideoIds(currentUserId, videoIds);

        return videos.stream().map(v -> {
            var uploader = userFacade.getUserProfile(v.getUploaderId(), currentUserId);
            int likeCount = likeCounts.getOrDefault(v.getId(), 0);
            int commentCount = commentCounts.getOrDefault(v.getId(), 0);
            boolean isLiked = likedIds.contains(v.getId());

            return new VideoItemResponse(
                    v.getId(),
                    v.getVideoUrl(),
                    v.getThumbnailUrl(),
                    v.getDescription(),
                    uploader != null ? uploader.username() : "Unknown",
                    uploader != null ? uploader.avatarUrl() : null,
                    v.getUploaderId(),
                    v.getDuration(),
                    v.getUploadedAt().toString(),
                    likeCount,
                    commentCount,
                    isLiked,
                    v.getIsAiGenerated() != null && v.getIsAiGenerated()
            );
        }).toList();
    }

    private boolean isVideoFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("video/");
    }
}
