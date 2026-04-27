package com.kynn.reevo_backend.video.internal.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kynn.reevo_backend.video.internal.domain.Video;

public interface VideoRepository extends JpaRepository<Video, UUID>{
  
}
