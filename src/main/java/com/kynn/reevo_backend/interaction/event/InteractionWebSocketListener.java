package com.kynn.reevo_backend.interaction.event;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InteractionWebSocketListener {

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleVideoLikeUpdated(VideoLikeUpdatedEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/video/" + event.getVideoId().toString() + "/likes",
                new VideoLikeUpdateMessage(event.getVideoId().toString(), event.getLikeCount(), event.isLiked())
        );
    }

    @EventListener
    public void handleVideoCommentUpdated(VideoCommentUpdatedEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/video/" + event.getVideoId().toString() + "/comments",
                new VideoCommentUpdateMessage(event.getVideoId().toString(), event.getCommentCount())
        );
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class VideoLikeUpdateMessage {
        private String videoId;
        private int likeCount;
        private boolean isLiked;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class VideoCommentUpdateMessage {
        private String videoId;
        private int commentCount;
    }
}
