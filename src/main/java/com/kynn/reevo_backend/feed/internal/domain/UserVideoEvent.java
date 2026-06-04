package com.kynn.reevo_backend.feed.internal.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_video_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVideoEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID videoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserVideoEventType eventType;

    private Long watchMs;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

