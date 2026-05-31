package com.kynn.reevo_backend.tag.internal.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kynn.reevo_backend.tag.internal.service.TaggingService;
import com.kynn.reevo_backend.video.event.VideoUploadedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoTaggingEventListener {

    private final TaggingService taggingService;

    @Async
    @EventListener
    public void handleVideoUploaded(VideoUploadedEvent event) {
        // Tagging is now handled in CloudinaryUploadService before publishing event
        // This listener is kept as a fallback (you can comment it out if desired)
        log.info("VideoTaggingEventListener received event for video: {}, but tagging was already done in CloudinaryUploadService", event.getVideo().getId());
        // taggingService.tagVideo(event.getVideo());
    }
}

