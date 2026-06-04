package com.kynn.reevo_backend.interaction.internal.repository;

import com.kynn.reevo_backend.interaction.internal.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByVideoIdOrderByCreatedAtDesc(UUID videoId);
    List<Comment> findByParentIdOrderByCreatedAtAsc(UUID parentId);
    long countByVideoId(UUID videoId);

    @Query("select c.videoId as videoId, count(c) as count from Comment c where c.videoId in :videoIds group by c.videoId")
    List<VideoIdCount> countByVideoIdIn(@Param("videoIds") List<UUID> videoIds);
}
