package com.kynn.reevo_backend.notification.internal.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kynn.reevo_backend.interaction.event.CommentAddedEvent;
import com.kynn.reevo_backend.notification.api.NotificationFacade;
import com.kynn.reevo_backend.notification.internal.domain.Notification;
import com.kynn.reevo_backend.user.api.UserFacade;
import com.kynn.reevo_backend.video.event.VideoUploadedEvent;
import com.kynn.reevo_backend.video.internal.repository.VideoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationFacade notificationFacade;
    private final VideoRepository videoRepository;
    private final UserFacade userFacade;

    @Async
    @EventListener
    public void handleVideoUploaded(VideoUploadedEvent event) {
        log.info("Handling VideoUploadedEvent for video {}", event.getVideo().getId());
        
        notificationFacade.sendNotification(
                event.getVideo().getUploaderId(),
                Notification.NotificationType.VIDEO_PUBLISHED,
                "Video Uploaded Successfully",
                "Your video '" + event.getVideo().getDescription() + "' is now ready for viewing.",
                "{\"videoId\": \"" + event.getVideo().getId() + "\"}"
        );
    }

    @Async
    @EventListener
    public void handleCommentAdded(CommentAddedEvent event) {
        log.info("Handling CommentAddedEvent for comment {}", event.getComment().getId());

        UUID videoOwnerId = videoRepository.findById(event.getComment().getVideoId())
                .map(v -> v.getUploaderId())
                .orElse(null);

        if (videoOwnerId == null || videoOwnerId.equals(event.getComment().getUserId())) {
            return;
        }

        String commenterName = userFacade.getUserProfile(event.getComment().getUserId()).username();

        notificationFacade.sendNotification(
                videoOwnerId,
                Notification.NotificationType.COMMENT_RECEIVED,
                "New Comment",
                commenterName + " commented on your video: " + event.getComment().getContent(),
                "{\"videoId\": \"" + event.getComment().getVideoId() + "\", \"commentId\": \"" + event.getComment().getId() + "\"}"
        );
    }
}
