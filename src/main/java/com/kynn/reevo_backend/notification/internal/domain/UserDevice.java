package com.kynn.reevo_backend.notification.internal.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "user_devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDevice {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID userDeviceId;

  @Column(nullable = false)
  private UUID userId;

  @Column(nullable = false, unique = true)
  private String fcmToken;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private DeviceType deviceType;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  public enum DeviceType {
    ANDROID,
    IOS,
    WEB
  }
}

