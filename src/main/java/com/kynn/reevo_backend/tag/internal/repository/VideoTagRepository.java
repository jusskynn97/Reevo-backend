package com.kynn.reevo_backend.tag.internal.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kynn.reevo_backend.tag.internal.domain.VideoTag;

public interface VideoTagRepository extends JpaRepository<VideoTag, UUID> {
    @Query("""
            select vt.videoId as videoId, vt.tag.id as tagId, vt.tag.name as tagName, vt.score as tagScore
            from VideoTag vt
            where vt.videoId in :videoIds
            """)
    List<VideoTagRow> findRowsByVideoIdIn(@Param("videoIds") List<UUID> videoIds);

    List<VideoTag> findByVideoId(UUID videoId);
}

