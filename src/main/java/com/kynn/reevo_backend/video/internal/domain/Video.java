package com.kynn.reevo_backend.video.internal.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
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

    private UUID uploaderId;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Boolean allowComment;

    @Enumerated(EnumType.STRING)
    private VideoPrivacy videoPrivacy;

    private String cloudinaryPublicId;
    private String videoUrl;           // secure_url from Cloudinary
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private VideoStatus status = VideoStatus.PENDING;

    private Long duration;             // seconds
    private Long fileSize;
    private String format;             // mp4, webm...
    private LocalDateTime scheduledAt = LocalDateTime.now();

    private LocalDateTime uploadedAt = LocalDateTime.now();
    private LocalDateTime processedAt;
    
    private Boolean isAiGenerated = false;
}
