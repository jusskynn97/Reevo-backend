package com.kynn.reevo_backend.notification.internal.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID notificationId;

  @Column(nullable = false)
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private NotificationType type;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String message;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "jsonb")
  private String metadata; // JSON data như meetingId, etc.

  @Column(nullable = false)
  private Boolean isRead = false;

  @CreationTimestamp
  private LocalDateTime createdAt;

  private LocalDateTime readAt;

  public enum NotificationType {
    VIDEO_PUBLISHED,
    VIDEO_FAILED,
    COMMENT_RECEIVED,
    REPLY_RECEIVED,
    VIDEO_LIKED,
    COMMENT_LIKED,
    MENTION
  }

}


