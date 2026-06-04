package com.kynn.reevo_backend.interaction.internal.repository;

import com.kynn.reevo_backend.interaction.internal.domain.VideoLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VideoLikeRepository extends JpaRepository<VideoLike, UUID> {
    Optional<VideoLike> findByVideoIdAndUserId(UUID videoId, UUID userId);
    long countByVideoId(UUID videoId);
    boolean existsByVideoIdAndUserId(UUID videoId, UUID userId);

    @Query("select vl.videoId as videoId, count(vl) as count from VideoLike vl where vl.videoId in :videoIds group by vl.videoId")
    List<VideoIdCount> countByVideoIdIn(@Param("videoIds") List<UUID> videoIds);

    List<VideoLike> findByUserIdAndVideoIdIn(UUID userId, List<UUID> videoIds);
}
