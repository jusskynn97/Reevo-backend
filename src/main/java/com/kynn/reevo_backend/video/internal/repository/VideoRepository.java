package com.kynn.reevo_backend.video.internal.repository;

import java.util.List;
import java.util.UUID;

import com.kynn.reevo_backend.video.internal.domain.VideoStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kynn.reevo_backend.video.internal.domain.Video;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VideoRepository extends JpaRepository<Video, UUID>{
  // Lấy danh sách video đầu tiên (khi cursor là null)
  List<Video> findByStatusOrderByUploadedAtDesc(VideoStatus status, Pageable pageable);

  // Lấy danh sách video tiếp theo dựa trên cursor (createdAt của video cuối cùng)
  @Query("SELECT v FROM Video v WHERE v.status = :status AND v.uploadedAt < :lastTimestamp ORDER BY v.uploadedAt DESC")
  List<Video> findFeedWithCursor(
          @Param("status") VideoStatus status,
          @Param("lastTimestamp") java.time.LocalDateTime lastTimestamp,
          Pageable pageable
  );

  // Lấy danh sách video của một người dùng
  List<Video> findByUploaderIdAndStatusOrderByUploadedAtDesc(UUID uploaderId, VideoStatus status);
}
