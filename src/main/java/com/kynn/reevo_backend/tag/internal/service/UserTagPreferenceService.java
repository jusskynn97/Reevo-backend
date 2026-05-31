package com.kynn.reevo_backend.tag.internal.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kynn.reevo_backend.tag.internal.domain.UserTagAffinity;
import com.kynn.reevo_backend.tag.internal.repository.UserTagAffinityRepository;
import com.kynn.reevo_backend.tag.internal.repository.VideoTagRepository;
import com.kynn.reevo_backend.video.internal.repository.VideoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserTagPreferenceService {

    private final VideoTagRepository videoTagRepository;
    private final UserTagAffinityRepository userTagAffinityRepository;
    private final VideoRepository videoRepository;

    public void updateFromLike(UUID userId, UUID videoId, double weight) {
        updateForVideo(userId, videoId, weight);
    }

    public void updateFromWatch(UUID userId, UUID videoId, long watchMs) {
        double ratio = videoRepository.findById(videoId)
                .map(v -> {
                    long durationSec = v.getDuration() == null ? 0 : v.getDuration();
                    if (durationSec <= 0) {
                        return 0.0;
                    }
                    return Math.min(1.0, (watchMs / (durationSec * 1000.0)));
                })
                .orElse(0.0);
        if (ratio <= 0) {
            return;
        }
        updateForVideo(userId, videoId, ratio);
    }

    public Map<UUID, Double> getVideoPreferenceScores(UUID userId, List<UUID> videoIds) {
        if (userId == null || videoIds == null || videoIds.isEmpty()) {
            return Map.of();
        }

        var rows = videoTagRepository.findRowsByVideoIdIn(videoIds);
        if (rows.isEmpty()) {
            return Map.of();
        }

        List<UUID> tagIds = new ArrayList<>();
        for (var r : rows) {
            tagIds.add(r.getTagId());
        }

        Map<UUID, Double> affinityByTagId = new HashMap<>();
        for (var uta : userTagAffinityRepository.findByUserIdAndTagIds(userId, tagIds)) {
            affinityByTagId.put(uta.getTag().getId(), uta.getScore());
        }

        Map<UUID, Double> scoreByVideoId = new HashMap<>();
        for (var r : rows) {
            double affinity = affinityByTagId.getOrDefault(r.getTagId(), 0.0);
            double tagScore = r.getTagScore() == null ? 0.0 : r.getTagScore();
            if (affinity == 0.0 || tagScore == 0.0) {
                continue;
            }
            scoreByVideoId.merge(r.getVideoId(), affinity * tagScore, Double::sum);
        }
        return scoreByVideoId;
    }

    private void updateForVideo(UUID userId, UUID videoId, double weight) {
        if (userId == null || videoId == null || weight <= 0) {
            return;
        }
        var tags = videoTagRepository.findByVideoId(videoId);
        if (tags.isEmpty()) {
            return;
        }
        for (var vt : tags) {
            var tag = vt.getTag();
            if (tag == null) {
                continue;
            }
            double delta = weight * (vt.getScore() == null ? 1.0 : vt.getScore());
            var existing = userTagAffinityRepository.findByUserIdAndTagIds(userId, List.of(tag.getId()));
            if (existing.isEmpty()) {
                userTagAffinityRepository.save(UserTagAffinity.builder()
                        .userId(userId)
                        .tag(tag)
                        .score(delta)
                        .build());
            } else {
                UserTagAffinity affinity = existing.get(0);
                affinity.setScore(affinity.getScore() + delta);
                userTagAffinityRepository.save(affinity);
            }
        }
    }
}

