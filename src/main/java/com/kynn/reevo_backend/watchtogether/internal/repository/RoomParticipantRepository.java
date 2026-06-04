
package com.kynn.reevo_backend.watchtogether.internal.repository;

import com.kynn.reevo_backend.watchtogether.internal.domain.RoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, UUID> {
    List<RoomParticipant> findByRoomId(UUID roomId);
    Optional<RoomParticipant> findByRoomIdAndUserId(UUID roomId, UUID userId);
    void deleteByRoomIdAndUserId(UUID roomId, UUID userId);
    void deleteByRoomId(UUID roomId);
    long countByRoomId(UUID roomId);
}

