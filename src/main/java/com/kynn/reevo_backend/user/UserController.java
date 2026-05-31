package com.kynn.reevo_backend.user;

import com.kynn.reevo_backend.common.api.ApiResponse;
import com.kynn.reevo_backend.common.api.CurrentUserId;
import com.kynn.reevo_backend.user.api.UserFacade;
import com.kynn.reevo_backend.user.api.dto.AuthResponse;
import com.kynn.reevo_backend.user.api.dto.RegisterRequest;
import com.kynn.reevo_backend.user.api.dto.UserSummary;
import com.kynn.reevo_backend.user.internal.config.CurrentUserIdArgumentResolver;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserFacade userFacade;

  @GetMapping("/profile")
  public ApiResponse<UserSummary> getMyProfile(@CurrentUserId UUID uuid) {
    return ApiResponse.ok(userFacade.getUserProfile(uuid, uuid));
  }

  @GetMapping("/{userId}/profile")
  public ApiResponse<UserSummary> getUserProfile(@PathVariable UUID userId, @CurrentUserId UUID currentUserId) {
    return ApiResponse.ok(userFacade.getUserProfile(userId, currentUserId));
  }

  @PostMapping("/{userId}/follow")
  public ApiResponse<Void> followUser(@PathVariable UUID userId, @CurrentUserId UUID currentUserId) {
    userFacade.followUser(currentUserId, userId);
    return ApiResponse.ok(null);
  }

  @DeleteMapping("/{userId}/follow")
  public ApiResponse<Void> unfollowUser(@PathVariable UUID userId, @CurrentUserId UUID currentUserId) {
    userFacade.unfollowUser(currentUserId, userId);
    return ApiResponse.ok(null);
  }
}
