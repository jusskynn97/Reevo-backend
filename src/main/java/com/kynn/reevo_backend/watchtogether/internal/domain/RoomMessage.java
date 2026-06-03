
package com.kynn.reevo_backend.watchtogether.internal.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "room_messages")
@Data
@NoArgsConstructor
public class RoomMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private Long version;

    private UUID roomId;
    private UUID senderId;
    private String senderUsername;
    private String senderAvatarUrl;

    @Column(columnDefinition = "TEXT")
    private String content;
    private String imageUrl;

    private LocalDateTime createdAt = LocalDateTime.now();
}

