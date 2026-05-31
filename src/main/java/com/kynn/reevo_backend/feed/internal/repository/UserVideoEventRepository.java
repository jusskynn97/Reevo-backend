package com.kynn.reevo_backend.feed.internal.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kynn.reevo_backend.feed.internal.domain.UserVideoEvent;

public interface UserVideoEventRepository extends JpaRepository<UserVideoEvent, UUID> {
}

