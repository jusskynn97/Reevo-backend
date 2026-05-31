package com.kynn.reevo_backend.interaction.event;

import com.kynn.reevo_backend.interaction.internal.domain.Comment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CommentAddedEvent extends ApplicationEvent {
    private final Comment comment;

    public CommentAddedEvent(Object source, Comment comment) {
        super(source);
        this.comment = comment;
    }
}
