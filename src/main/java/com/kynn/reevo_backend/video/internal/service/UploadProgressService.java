package com.kynn.reevo_backend.video.internal.service;

import com.kynn.reevo_backend.video.internal.domain.VideoStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UploadProgressService {

  // Map từ videoId → SseEmitter của client đang chờ
  private final ConcurrentHashMap<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

  public SseEmitter createEmitter(UUID videoId) {
    // Timeout 5 phút — đủ cho upload lớn
    SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);
    emitters.put(videoId, emitter);

    emitter.onCompletion(() -> emitters.remove(videoId));
    emitter.onTimeout(() -> emitters.remove(videoId));
    emitter.onError(e -> emitters.remove(videoId));

    return emitter;
  }

  public void sendProgress(UUID videoId, VideoStatus status, int percent) {
    SseEmitter emitter = emitters.get(videoId);
    if (emitter == null) return; // Client đã disconnect

    try {
      Map<String, Object> payload = Map.of(
              "videoId", videoId,
              "status",  status.name(),
              "percent", percent
      );
      emitter.send(SseEmitter.event()
              .name("upload-progress")
              .data(payload));

      if (status == VideoStatus.READY || status == VideoStatus.FAILED) {
        emitter.complete(); // Đóng stream khi xong
      }
    } catch (IOException e) {
      log.warn("SSE send failed for video {}, client likely disconnected", videoId);
      emitter.completeWithError(e);
      emitters.remove(videoId);
    }
  }
}