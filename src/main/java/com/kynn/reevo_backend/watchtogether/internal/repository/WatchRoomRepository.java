
package com.kynn.reevo_backend.watchtogether.internal.repository;

import com.kynn.reevo_backend.watchtogether.internal.domain.RoomPrivacy;
import com.kynn.reevo_backend.watchtogether.internal.domain.WatchRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WatchRoomRepository extends JpaRepository<WatchRoom, UUID> {
    List<WatchRoom> findByPrivacyAndIsActiveTrueOrderByCreatedAtDesc(RoomPrivacy privacy);
    List<WatchRoom> findByCreatorIdAndIsActiveTrue(UUID creatorId);
}

