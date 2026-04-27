package com.kynn.reevo_backend.video.internal.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.kynn.reevo_backend.video.event.VideoUploadedEvent;
import com.kynn.reevo_backend.video.internal.domain.Video;
import com.kynn.reevo_backend.video.internal.domain.VideoStatus;
import com.kynn.reevo_backend.video.internal.repository.VideoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryUploadService {

    private final Cloudinary cloudinary;
    private final ApplicationEventPublisher eventPublisher;
    private final VideoRepository videoRepository;

    @Async("videoUploadExecutor")
    public CompletableFuture<Void> uploadAsync(UUID videoId, byte[] fileBytes) {
        try {
            updateVideoStatusWithRetry(videoId, VideoStatus.UPLOADING, null);

            Map<String, Object> options = new HashMap<>();
            options.put("resource_type", "video");
            options.put("folder", "short_videos/" + getVideoUploaderId(videoId));
            options.put("public_id", "video_" + videoId);
            options.put("chunk_size", 20000000); // 20MB chunks

            // Upload large (hỗ trợ >100MB)
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().uploadLarge(fileBytes, options);

            // Cập nhật thông tin sau khi upload thành công
            updateVideoWithUploadResult(videoId, uploadResult);

            // Publish event cho các module khác (feed, notification...)
            Optional<Video> video = videoRepository.findById(videoId);
            video.ifPresent(v -> eventPublisher.publishEvent(new VideoUploadedEvent(this, v)));

            return CompletableFuture.completedFuture(null);

        } catch (OptimisticLockingFailureException e) {
            log.error("Upload video failed for id: {} - Optimistic lock exceeded max retries", videoId, e);
            updateVideoStatusWithRetry(videoId, VideoStatus.FAILED, null);
            throw new RuntimeException("Upload failed - concurrent modification", e);
        } catch (IOException e) {
            log.error("Upload video failed for id: {} - IO error", videoId, e);
            updateVideoStatusWithRetry(videoId, VideoStatus.FAILED, null);
            throw new RuntimeException("Upload failed - IO error", e);
        } catch (RuntimeException e) {
            log.error("Upload video failed for id: {}", videoId, e);
            updateVideoStatusWithRetry(videoId, VideoStatus.FAILED, null);
            throw e;
        }
    }

    @Transactional
    private void updateVideoStatusWithRetry(UUID videoId, VideoStatus status, LocalDateTime processedAt) {
        int maxRetries = 3;
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                Optional<Video> videoOpt = videoRepository.findById(videoId);
                if (videoOpt.isPresent()) {
                    Video video = videoOpt.get();
                    video.setStatus(status);
                    if (processedAt != null) {
                        video.setProcessedAt(processedAt);
                    }
                    videoRepository.save(video);
                    return;
                }
                log.warn("Video not found: {}", videoId);
                return;
            } catch (OptimisticLockingFailureException e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    log.error("Max retries exceeded for video status update: {}", videoId, e);
                    throw e;
                }
                log.warn("Optimistic lock retry {} for video: {}", retryCount, videoId);
                backoffSleep(retryCount);
            }
        }
    }

    @Transactional
    private void updateVideoWithUploadResult(UUID videoId, Map<String, Object> uploadResult) {
        int maxRetries = 3;
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                Optional<Video> videoOpt = videoRepository.findById(videoId);
                if (videoOpt.isPresent()) {
                    Video video = videoOpt.get();
                    video.setCloudinaryPublicId((String) uploadResult.get("public_id"));
                    video.setVideoUrl((String) uploadResult.get("secure_url"));
                    video.setThumbnailUrl(cloudinary.url().transformation(new Transformation<>().width(300).crop("fill")).generate((String) uploadResult.get("public_id")));
                    video.setDuration(((Double) uploadResult.get("duration")).longValue());
                    video.setFileSize(((Integer) uploadResult.get("bytes")).longValue());
                    video.setFormat((String) uploadResult.get("format"));
                    video.setStatus(VideoStatus.READY);
                    video.setProcessedAt(LocalDateTime.now());
                    videoRepository.save(video);
                    return;
                }
                log.warn("Video not found: {}", videoId);
                return;
            } catch (OptimisticLockingFailureException e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    log.error("Max retries exceeded for video update: {}", videoId, e);
                    throw e;
                }
                log.warn("Optimistic lock retry {} for video: {}", retryCount, videoId);
                backoffSleep(retryCount);
            }
        }
    }

    private void backoffSleep(int retryCount) {
        try {
            Thread.sleep(100L * retryCount); // Exponential backoff
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(ie);
        }
    }

    @Transactional(readOnly = true)
    private UUID getVideoUploaderId(UUID videoId) {
        return videoRepository.findById(videoId)
                .map(Video::getUploaderId)
                .orElse(UUID.randomUUID());
    }
}
