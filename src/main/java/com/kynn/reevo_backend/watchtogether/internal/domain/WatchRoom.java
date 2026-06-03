
package com.kynn.reevo_backend.watchtogether.internal.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
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
@Table(name = "watch_rooms")
@Data
@NoArgsConstructor
public class WatchRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;

    private UUID creatorId;
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String videoId;
    private String videoUrl;
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private RoomPrivacy privacy = RoomPrivacy.PUBLIC;

    private Boolean isActive = true;
    private Integer maxUsers = 20;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}

