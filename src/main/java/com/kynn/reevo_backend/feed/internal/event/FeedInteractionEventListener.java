package com.kynn.reevo_backend.feed.internal.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.kynn.reevo_backend.feed.internal.service.FeedEventService;
import com.kynn.reevo_backend.interaction.event.CommentAddedEvent;
import com.kynn.reevo_backend.interaction.event.VideoLikeUpdatedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FeedInteractionEventListener {

    private final FeedEventService feedEventService;

    @EventListener
    public void onVideoLikeUpdated(VideoLikeUpdatedEvent event) {
        if (event.isLiked()) {
            feedEventService.recordLike(event.getUserId(), event.getVideoId());
        } else {
            feedEventService.recordUnlike(event.getUserId(), event.getVideoId());
        }
    }

    @EventListener
    public void onCommentAdded(CommentAddedEvent event) {
        var comment = event.getComment();
        feedEventService.recordComment(comment.getUserId(), comment.getVideoId());
    }
}

