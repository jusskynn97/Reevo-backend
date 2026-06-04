package com.kynn.reevo_backend.feed.internal.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kynn.reevo_backend.feed.api.FeedFacade;
import com.kynn.reevo_backend.feed.api.dto.FeedResponse;
import com.kynn.reevo_backend.feed.internal.client.FeedRecommendationClient;
import com.kynn.reevo_backend.feed.internal.client.dto.RecoCandidate;
import com.kynn.reevo_backend.feed.internal.client.dto.RecoRankRequest;
import com.kynn.reevo_backend.interaction.api.InteractionFacade;
import com.kynn.reevo_backend.tag.internal.service.UserTagPreferenceService;
import com.kynn.reevo_backend.user.api.UserFacade;
import com.kynn.reevo_backend.user.internal.repository.FollowRepository;
import com.kynn.reevo_backend.video.api.dto.VideoItemResponse;
import com.kynn.reevo_backend.video.internal.domain.Video;
import com.kynn.reevo_backend.video.internal.domain.VideoStatus;
import com.kynn.reevo_backend.video.internal.repository.VideoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FeedService implements FeedFacade {

    private final VideoRepository videoRepository;
    private final FollowRepository followRepository;
    private final UserFacade userFacade;
    @Lazy
    private final InteractionFacade interactionFacade;
    private final FeedRecommendationClient feedRecommendationClient;
    private final UserTagPreferenceService userTagPreferenceService;

    @Override
    public FeedResponse getVideoFeed(LocalDateTime cursor, int size, UUID currentUserId) {
        int candidateSize = Math.min(Math.max(size * 50, 200), 800);
        Pageable pageable = PageRequest.of(0, candidateSize);

        List<Video> candidates;
        if (cursor == null) {
            candidates = videoRepository.findByStatusOrderByUploadedAtDesc(VideoStatus.READY, pageable);
        } else {
            candidates = videoRepository.findFeedWithCursor(VideoStatus.READY, cursor, pageable);
        }

        Set<UUID> followingUploaderIds = new HashSet<>();
        if (currentUserId != null) {
            followingUploaderIds.addAll(followRepository.findFollowingIdsByFollowerId(currentUserId));
        }

        List<UUID> candidateIds = candidates.stream().map(Video::getId).toList();
        Map<UUID, Integer> likeCounts = interactionFacade.getLikeCounts(candidateIds);
        Map<UUID, Integer> commentCounts = interactionFacade.getCommentCounts(candidateIds);
        Map<UUID, Double> tagPrefScores = currentUserId == null ? Map.of() : userTagPreferenceService.getVideoPreferenceScores(currentUserId, candidateIds);

        List<Video> ranked = rankWithOptionalRecoService(candidates, currentUserId, followingUploaderIds, likeCounts, commentCounts, tagPrefScores, size);

        List<UUID> rankedIds = ranked.stream().map(Video::getId).toList();
        Set<UUID> likedIds = currentUserId == null ? Set.of() : interactionFacade.getLikedVideoIds(currentUserId, rankedIds);

        List<VideoItemResponse> items = ranked.stream().map(v -> {
            var uploader = userFacade.getUserProfile(v.getUploaderId(), currentUserId);
            int likeCount = likeCounts.getOrDefault(v.getId(), 0);
            int commentCount = commentCounts.getOrDefault(v.getId(), 0);
            boolean isLiked = likedIds.contains(v.getId());

            return new VideoItemResponse(
                    v.getId(),
                    v.getVideoUrl(),
                    v.getThumbnailUrl(),
                    v.getDescription(),
                    uploader != null ? uploader.username() : "Unknown",
                    uploader != null ? uploader.avatarUrl() : null,
                    v.getUploaderId(),
                    v.getDuration(),
                    v.getUploadedAt().toString(),
                    likeCount,
                    commentCount,
                    isLiked,
                    v.getIsAiGenerated() != null && v.getIsAiGenerated()
            );
        }).toList();

        String nextCursor = null;
        if (!ranked.isEmpty()) {
            nextCursor = ranked.get(ranked.size() - 1).getUploadedAt().toString();
        }

        return new FeedResponse(items, nextCursor);
    }

    private List<Video> rankWithOptionalRecoService(
            List<Video> candidates,
            UUID currentUserId,
            Set<UUID> followingUploaderIds,
            Map<UUID, Integer> likeCounts,
            Map<UUID, Integer> commentCounts,
            Map<UUID, Double> tagPrefScores,
            int size) {
        List<RecoCandidate> recoCandidates = candidates.stream()
                .map(v -> new RecoCandidate(
                        v.getId(),
                        v.getUploaderId(),
                        v.getUploadedAt(),
                        followingUploaderIds.contains(v.getUploaderId()),
                        likeCounts.getOrDefault(v.getId(), 0),
                        commentCounts.getOrDefault(v.getId(), 0),
                        v.getDuration() == null ? 0 : v.getDuration(),
                        tagPrefScores.getOrDefault(v.getId(), 0.0)
                ))
                .toList();

        log.info("Calling recommendation service with {} candidates and userId: {}", candidates.size(), currentUserId);
        log.info("Tag preference scores available for {} videos", tagPrefScores.size());
        tagPrefScores.forEach((vid, score) -> log.info("Video {} has tag preference score: {}", vid, score));

        List<UUID> rankedIds = feedRecommendationClient.rank(new RecoRankRequest(currentUserId, recoCandidates));
        if (rankedIds != null && !rankedIds.isEmpty()) {
            log.info("Recommendation service returned {} ranked IDs", rankedIds.size());
            log.info("First 5 ranked IDs: {}", rankedIds.stream().limit(5).toList());
            Map<UUID, Video> byId = candidates.stream().collect(Collectors.toMap(Video::getId, Function.identity(), (a, b) -> a));
            List<Video> result = rankedIds.stream()
                    .map(byId::get)
                    .filter(v -> v != null)
                    .limit(size)
                    .toList();
            log.info("Returning {} videos from recommendation service", result.size());
            return result;
        }

        log.warn("Recommendation service returned empty or null, falling back to default scoring");
        return candidates.stream()
                .sorted(Comparator.comparingDouble(v -> -score(v, followingUploaderIds, tagPrefScores.getOrDefault(v.getId(), 0.0))))
                .limit(size)
                .toList();
    }

    private double score(Video video, Set<UUID> followingUploaderIds, double tagPreferenceScore) {
        double score = 0;
        if (followingUploaderIds.contains(video.getUploaderId())) {
            score += 10;
        }
        long hours = Duration.between(video.getUploadedAt(), LocalDateTime.now()).toHours();
        score += Math.max(0, 72 - hours) * 0.1;
        score += Math.min(5.0, tagPreferenceScore) * 2.0;
        return score;
    }
}
