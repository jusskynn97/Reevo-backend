package com.kynn.reevo_backend.video.event;

import org.springframework.context.ApplicationEvent;

import com.kynn.reevo_backend.video.internal.domain.Video;

import lombok.Getter;

@Getter
public class VideoUploadedEvent extends ApplicationEvent {
    private final Video video;

    public VideoUploadedEvent(Object source, Video video) {
        super(source);
        this.video = video;
    }
}
