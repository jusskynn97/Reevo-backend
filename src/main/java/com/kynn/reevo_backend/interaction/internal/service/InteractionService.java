package com.kynn.reevo_backend.interaction.internal.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kynn.reevo_backend.common.exception.ResourceNotFoundException;
import com.kynn.reevo_backend.interaction.api.InteractionFacade;
import com.kynn.reevo_backend.interaction.api.dto.CommentRequest;
import com.kynn.reevo_backend.interaction.api.dto.CommentResponse;
import com.kynn.reevo_backend.interaction.event.CommentAddedEvent;
import com.kynn.reevo_backend.interaction.event.VideoCommentUpdatedEvent;
import com.kynn.reevo_backend.interaction.event.VideoLikeUpdatedEvent;
import com.kynn.reevo_backend.interaction.internal.domain.Comment;
import com.kynn.reevo_backend.interaction.internal.domain.CommentLike;
import com.kynn.reevo_backend.interaction.internal.domain.VideoLike;
import com.kynn.reevo_backend.interaction.internal.repository.CommentLikeRepository;
import com.kynn.reevo_backend.interaction.internal.repository.CommentRepository;
import com.kynn.reevo_backend.interaction.internal.repository.VideoLikeRepository;
import com.kynn.reevo_backend.user.api.UserFacade;
import com.kynn.reevo_backend.user.api.dto.UserSummary;
import com.kynn.reevo_backend.video.internal.repository.VideoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InteractionService implements InteractionFacade {

    private final VideoLikeRepository videoLikeRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final VideoRepository videoRepository;
    private final UserFacade userFacade;
    private final ApplicationEventPublisher eventPublisher;

    public void likeVideo(UUID videoId, UUID userId) {
        if (!videoRepository.existsById(videoId)) {
            throw new ResourceNotFoundException("Video not found");
        }
        if (videoLikeRepository.existsByVideoIdAndUserId(videoId, userId)) {
            return; // Already liked
        }
        VideoLike like = VideoLike.builder()
                .videoId(videoId)
                .userId(userId)
                .build();
        videoLikeRepository.save(like);
        
        // Publish event for real-time update
        int likeCount = (int) videoLikeRepository.countByVideoId(videoId);
        eventPublisher.publishEvent(new VideoLikeUpdatedEvent(videoId, likeCount, true, userId));
    }

    public void unlikeVideo(UUID videoId, UUID userId) {
        videoLikeRepository.findByVideoIdAndUserId(videoId, userId)
                .ifPresent(like -> {
                    videoLikeRepository.delete(like);
                    // Publish event for real-time update
                    int likeCount = (int) videoLikeRepository.countByVideoId(videoId);
                    eventPublisher.publishEvent(new VideoLikeUpdatedEvent(videoId, likeCount, false, userId));
                });
    }

    public CommentResponse commentVideo(UUID videoId, UUID userId, CommentRequest request) {
        if (!videoRepository.existsById(videoId)) {
            throw new ResourceNotFoundException("Video not found");
        }

        Comment comment = Comment.builder()
                .videoId(videoId)
                .userId(userId)
                .content(request.getContent())
                .parentId(request.getParentId())
                .build();

        if (request.getParentId() != null) {
            if (!commentRepository.existsById(request.getParentId())) {
                throw new ResourceNotFoundException("Parent comment not found");
            }
        }

        Comment savedComment = commentRepository.save(comment);
        
        // Publish events
        eventPublisher.publishEvent(new CommentAddedEvent(this, savedComment));
        
        int commentCount = (int) commentRepository.countByVideoId(videoId);
        eventPublisher.publishEvent(new VideoCommentUpdatedEvent(videoId, commentCount, userId));
        
        return mapToResponse(savedComment);
    }

    public void likeComment(UUID commentId, UUID userId) {
        if (!commentRepository.existsById(commentId)) {
            throw new ResourceNotFoundException("Comment not found");
        }
        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            return;
        }
        CommentLike like = CommentLike.builder()
                .commentId(commentId)
                .userId(userId)
                .build();
        commentLikeRepository.save(like);
    }

    public void unlikeComment(UUID commentId, UUID userId) {
        commentLikeRepository.findByCommentIdAndUserId(commentId, userId)
                .ifPresent(commentLikeRepository::delete);
    }

    public List<CommentResponse> getCommentsByVideo(UUID videoId) {
        return commentRepository.findByVideoIdOrderByCreatedAtDesc(videoId).stream()
                .filter(c -> c.getParentId() == null) // Get top-level comments
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CommentResponse> getReplies(UUID commentId) {
        return commentRepository.findByParentIdOrderByCreatedAtAsc(commentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse mapToResponse(Comment comment) {
        UserSummary user = userFacade.getUserProfile(comment.getUserId());
        return CommentResponse.builder()
                .id(comment.getId())
                .videoId(comment.getVideoId())
                .userId(comment.getUserId())
                .username(user != null ? user.username() : "Unknown")
                .avatarUrl(user != null ? user.avatarUrl() : null)
                .content(comment.getContent())
                .parentId(comment.getParentId())
                .createdAt(comment.getCreatedAt())
                .likeCount(commentLikeRepository.countByCommentId(comment.getId()))
                .build();
    }

    @Override
    public int getLikeCount(UUID videoId) {
        return (int) videoLikeRepository.countByVideoId(videoId);
    }

    @Override
    public int getCommentCount(UUID videoId) {
        return (int) commentRepository.countByVideoId(videoId);
    }

    @Override
    public boolean isVideoLikedByUser(UUID videoId, UUID userId) {
        return videoLikeRepository.existsByVideoIdAndUserId(videoId, userId);
    }

    @Override
    public Map<UUID, Integer> getLikeCounts(List<UUID> videoIds) {
        if (videoIds == null || videoIds.isEmpty()) {
            return Map.of();
        }
        Map<UUID, Integer> result = new HashMap<>();
        for (var row : videoLikeRepository.countByVideoIdIn(videoIds)) {
            result.put(row.getVideoId(), (int) row.getCount());
        }
        return result;
    }

    @Override
    public Map<UUID, Integer> getCommentCounts(List<UUID> videoIds) {
        if (videoIds == null || videoIds.isEmpty()) {
            return Map.of();
        }
        Map<UUID, Integer> result = new HashMap<>();
        for (var row : commentRepository.countByVideoIdIn(videoIds)) {
            result.put(row.getVideoId(), (int) row.getCount());
        }
        return result;
    }

    @Override
    public Set<UUID> getLikedVideoIds(UUID userId, List<UUID> videoIds) {
        if (userId == null || videoIds == null || videoIds.isEmpty()) {
            return Set.of();
        }
        return videoLikeRepository.findByUserIdAndVideoIdIn(userId, videoIds)
                .stream()
                .map(vl -> vl.getVideoId())
                .collect(Collectors.toCollection(HashSet::new));
    }
}
