package com.kynn.reevo_backend.user.internal.repository;

import com.kynn.reevo_backend.user.internal.domain.Account;
import com.kynn.reevo_backend.user.internal.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, UUID> {
    Optional<Follow> findByFollowerIdAndFollowingId(UUID followerId, UUID followingId);
    boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId);
    long countByFollowingId(UUID followingId);
    long countByFollowerId(UUID followerId);
    void deleteByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    @Query("select f.following.id from Follow f where f.follower.id = :followerId")
    List<UUID> findFollowingIdsByFollowerId(@Param("followerId") UUID followerId);
}
