package com.kynn.reevo_backend.tag.internal.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kynn.reevo_backend.tag.internal.client.VideoTaggingClient;
import com.kynn.reevo_backend.tag.internal.client.dto.TaggingRequest;
import com.kynn.reevo_backend.tag.internal.domain.Tag;
import com.kynn.reevo_backend.tag.internal.domain.VideoTag;
import com.kynn.reevo_backend.tag.internal.repository.TagRepository;
import com.kynn.reevo_backend.tag.internal.repository.VideoTagRepository;
import com.kynn.reevo_backend.video.internal.domain.Video;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TaggingService {

    private final VideoTaggingClient videoTaggingClient;
    private final TagRepository tagRepository;
    private final VideoTagRepository videoTagRepository;

    public void tagVideo(Video video) {
        if (video == null || video.getId() == null) {
            log.warn("Skipping tagging: video or video ID is null");
            return;
        }

        log.info("Tagging video: {}", video.getId());
        var response = videoTaggingClient.tag(new TaggingRequest(
                video.getId(),
                video.getVideoUrl(),
                video.getThumbnailUrl(),
                video.getDescription()
        ));
        if (response == null || response.tags() == null || response.tags().isEmpty()) {
            log.warn("No tags returned for video: {}", video.getId());
            return;
        }
        log.info("Received {} tags for video {}: {}", response.tags().size(), video.getId(), response.tags());

        Map<String, Tag> tagByName = new HashMap<>();
        for (var t : response.tags()) {
            if (t == null || t.name() == null || t.name().isBlank() || t.score() == null) {
                continue;
            }
            Tag tag = tagRepository.findByName(t.name()).orElseGet(() -> tagRepository.save(Tag.builder().name(t.name()).build()));
            tagByName.put(tag.getName(), tag);
        }

        List<VideoTag> existing = videoTagRepository.findByVideoId(video.getId());
        for (var vt : existing) {
            videoTagRepository.delete(vt);
        }

        for (var t : response.tags()) {
            if (t == null || t.name() == null || t.name().isBlank() || t.score() == null) {
                continue;
            }
            Tag tag = tagByName.get(t.name());
            if (tag == null) {
                continue;
            }
            videoTagRepository.save(VideoTag.builder()
                    .videoId(video.getId())
                    .tag(tag)
                    .score(t.score())
                    .build());
        }
    }
}

