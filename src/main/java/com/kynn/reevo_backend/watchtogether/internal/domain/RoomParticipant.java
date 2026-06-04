
package com.kynn.reevo_backend.watchtogether.internal.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "room_participants")
@Data
@NoArgsConstructor
public class RoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;

    private UUID roomId;
    private UUID userId;
    private String username;
    private String avatarUrl;

    private Boolean isMuted = false;
    private Boolean isSpeaking = false;

    private LocalDateTime joinedAt = LocalDateTime.now();
}

