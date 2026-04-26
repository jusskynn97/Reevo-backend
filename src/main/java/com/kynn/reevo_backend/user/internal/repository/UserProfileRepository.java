package com.kynn.reevo_backend.user.internal.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kynn.reevo_backend.user.internal.domain.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> { }