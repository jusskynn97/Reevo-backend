
package com.kynn.reevo_backend.watchtogether.internal.repository;

import com.kynn.reevo_backend.watchtogether.internal.domain.RoomMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RoomMessageRepository extends JpaRepository<RoomMessage, UUID> {
    List<RoomMessage> findByRoomIdOrderByCreatedAtDesc(UUID roomId);
    List<RoomMessage> findByRoomIdAndCreatedAtAfterOrderByCreatedAtAsc(UUID roomId, LocalDateTime after);
    void deleteByCreatedAtBefore(LocalDateTime before);
    void deleteByRoomId(UUID roomId);
}

