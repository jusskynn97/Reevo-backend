package com.kynn.reevo_backend.tag.internal.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kynn.reevo_backend.tag.internal.domain.Tag;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    Optional<Tag> findByName(String name);
}

