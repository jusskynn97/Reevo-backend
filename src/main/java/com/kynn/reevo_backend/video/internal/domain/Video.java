package com.kynn.reevo_backend.video.internal.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "videos")
@Data
@NoArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;

    private UUID uploaderId;           // from user module

    private String title;
    private String description;
    private String hashtags;           // save as json or string

    private String cloudinaryPublicId;
    private String videoUrl;           // secure_url from Cloudinary
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private VideoStatus status = VideoStatus.PENDING;

    private Long duration;             // seconds
    private Long fileSize;
    private String format;             // mp4, webm...

    private LocalDateTime uploadedAt = LocalDateTime.now();
    private LocalDateTime processedAt;
}
