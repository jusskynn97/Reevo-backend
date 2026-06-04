package com.kynn.reevo_backend.video.internal.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "scheduled_videos")
@Data
@NoArgsConstructor
public class ScheduledVideo {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private UUID uploaderId;

  @Column(length = 2000)
  private String description;

  @Enumerated(EnumType.STRING)
  private VideoPrivacy privacy = VideoPrivacy.PUBLIC;

  private Boolean allowComment = true;

  private String cloudinaryPublicId;
  private String videoUrl;
  private String thumbnailUrl;

  private Long duration;
  private Long fileSize;

  private LocalDateTime scheduledAt;

//  @Enumerated(EnumType.STRING)
//  private VideoStatus status = VideoStatus.SCHEDULED;

  private LocalDateTime createdAt = LocalDateTime.now();
}