package com.kynn.reevo_backend.interaction;

import com.kynn.reevo_backend.common.api.ApiResponse;
import com.kynn.reevo_backend.common.api.CurrentUserId;
import com.kynn.reevo_backend.common.ratelimit.RateLimit;
import com.kynn.reevo_backend.interaction.api.dto.CommentRequest;
import com.kynn.reevo_backend.interaction.api.dto.CommentResponse;
import com.kynn.reevo_backend.interaction.internal.service.InteractionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
public class InteractionController {

    private final InteractionService interactionService;

    @PostMapping("/videos/{videoId}/like")
    @RateLimit(limit = 5, duration = 10)
    public ApiResponse<Void> likeVideo(@PathVariable UUID videoId, @CurrentUserId UUID userId) {
        interactionService.likeVideo(videoId, userId);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/videos/{videoId}/like")
    @RateLimit(limit = 5, duration = 10)
    public ApiResponse<Void> unlikeVideo(@PathVariable UUID videoId, @CurrentUserId UUID userId) {
        interactionService.unlikeVideo(videoId, userId);
        return ApiResponse.ok(null);
    }

    @PostMapping("/videos/{videoId}/comments")
    @RateLimit(limit = 3, duration = 60)
    public ApiResponse<CommentResponse> commentVideo(
            @PathVariable UUID videoId,
            @CurrentUserId UUID userId,
            @Valid @RequestBody CommentRequest request) {
        return ApiResponse.ok(interactionService.commentVideo(videoId, userId, request));
    }

    @GetMapping("/videos/{videoId}/comments")
    public ApiResponse<List<CommentResponse>> getComments(@PathVariable UUID videoId) {
        return ApiResponse.ok(interactionService.getCommentsByVideo(videoId));
    }

    @PostMapping("/comments/{commentId}/like")
    @RateLimit(limit = 10, duration = 60)
    public ApiResponse<Void> likeComment(@PathVariable UUID commentId, @CurrentUserId UUID userId) {
        interactionService.likeComment(commentId, userId);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/comments/{commentId}/like")
    @RateLimit(limit = 10, duration = 60)
    public ApiResponse<Void> unlikeComment(@PathVariable UUID commentId, @CurrentUserId UUID userId) {
        interactionService.unlikeComment(commentId, userId);
        return ApiResponse.ok(null);
    }

    @GetMapping("/comments/{commentId}/replies")
    public ApiResponse<List<CommentResponse>> getReplies(@PathVariable UUID commentId) {
        return ApiResponse.ok(interactionService.getReplies(commentId));
    }
}
