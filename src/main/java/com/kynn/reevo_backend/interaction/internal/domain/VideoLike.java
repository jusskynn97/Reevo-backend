package com.kynn.reevo_backend.interaction.internal.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "video_likes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"videoId", "userId"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoLike {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID videoId;

    @Column(nullable = false)
    private UUID userId;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
