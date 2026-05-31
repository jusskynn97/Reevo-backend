package com.kynn.reevo_backend.tag.internal.repository;

import java.util.UUID;

public interface VideoTagRow {
    UUID getVideoId();
    UUID getTagId();
    String getTagName();
    Double getTagScore();
}

