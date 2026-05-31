package com.kynn.reevo_backend.tag.internal.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kynn.reevo_backend.tag.internal.domain.UserTagAffinity;

public interface UserTagAffinityRepository extends JpaRepository<UserTagAffinity, UUID> {
    @Query("select uta from UserTagAffinity uta where uta.userId = :userId and uta.tag.id in :tagIds")
    List<UserTagAffinity> findByUserIdAndTagIds(@Param("userId") UUID userId, @Param("tagIds") List<UUID> tagIds);
}

