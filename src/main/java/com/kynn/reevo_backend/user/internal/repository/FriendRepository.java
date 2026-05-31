package com.kynn.reevo_backend.user.internal.repository;

import com.kynn.reevo_backend.user.internal.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendRepository extends JpaRepository<Friend, UUID> {

    @Query("SELECT f FROM Friend f WHERE (f.user1.id = :user1Id AND f.user2.id = :user2Id) OR (f.user1.id = :user2Id AND f.user2.id = :user1Id)")
    Optional<Friend> findFriendship(@Param("user1Id") UUID user1Id, @Param("user2Id") UUID user2Id);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Friend f WHERE (f.user1.id = :user1Id AND f.user2.id = :user2Id) OR (f.user1.id = :user2Id AND f.user2.id = :user1Id)")
    boolean existsFriendship(@Param("user1Id") UUID user1Id, @Param("user2Id") UUID user2Id);

    @Query("SELECT COUNT(f) FROM Friend f WHERE f.user1.id = :userId OR f.user2.id = :userId")
    long countFriendships(@Param("userId") UUID userId);
}

