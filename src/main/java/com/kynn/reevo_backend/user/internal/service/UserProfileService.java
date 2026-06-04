package com.kynn.reevo_backend.user.internal.service;

import com.kynn.reevo_backend.common.exception.ResourceNotFoundException;
import com.kynn.reevo_backend.user.api.UserFacade;
import com.kynn.reevo_backend.user.api.dto.AuthResponse;
import com.kynn.reevo_backend.user.api.dto.LoginRequest;
import com.kynn.reevo_backend.user.api.dto.RegisterRequest;
import com.kynn.reevo_backend.user.api.dto.UserSummary;
import com.kynn.reevo_backend.user.internal.domain.Account;
import com.kynn.reevo_backend.user.internal.domain.Follow;
import com.kynn.reevo_backend.user.internal.domain.Friend;
import com.kynn.reevo_backend.user.internal.domain.UserProfile;
import com.kynn.reevo_backend.user.internal.repository.AccountRepository;
import com.kynn.reevo_backend.user.internal.repository.FollowRepository;
import com.kynn.reevo_backend.user.internal.repository.FriendRepository;
import com.kynn.reevo_backend.user.internal.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import org.springframework.context.annotation.Primary;

@Service
@Primary
@RequiredArgsConstructor
@Transactional
public class UserProfileService implements UserFacade {

    private final AccountRepository accountRepository;
    private final UserProfileRepository userProfileRepository;
    private final FollowRepository followRepository;
    private final FriendRepository friendRepository;
    private final AuthService authService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        return authService.register(request);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return authService.login(request);
    }

    @Override
    public UserSummary getUserProfile(UUID userId) {
        return getUserProfile(userId, null);
    }

    @Override
    public UserSummary getUserProfile(UUID userId, UUID currentUserId) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UserProfile profile = userProfileRepository.findById(userId)
                .orElse(new UserProfile());

        long followerCount = followRepository.countByFollowingId(userId);
        long followingCount = followRepository.countByFollowerId(userId);
        long friendCount = friendRepository.countFriendships(userId);

        boolean isFollowing = false;
        boolean isFriend = false;

        if (currentUserId != null) {
            isFollowing = followRepository.existsByFollowerIdAndFollowingId(currentUserId, userId);
            isFriend = friendRepository.existsFriendship(currentUserId, userId);
        }

        return new UserSummary(
                account.getId(),
                account.getUsername(),
                profile.getDisplayName(),
                profile.getAvatarUrl(),
                profile.getBio(),
                followerCount,
                followingCount,
                friendCount,
                isFollowing,
                isFriend
        );
    }

    @Override
    public void logout(String refreshToken) {
        authService.logout(refreshToken);
    }

    @Override
    public void followUser(UUID followerId, UUID followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }

        Account follower = accountRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("Follower not found"));
        Account following = accountRepository.findById(followingId)
                .orElseThrow(() -> new ResourceNotFoundException("User to follow not found"));

        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            return;
        }

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        followRepository.save(follow);

        // Check if following also follows follower → make them friends
        if (followRepository.existsByFollowerIdAndFollowingId(followingId, followerId)
                && !friendRepository.existsFriendship(followerId, followingId)) {
            Friend friend = new Friend();
            friend.setUser1(follower);
            friend.setUser2(following);
            friendRepository.save(friend);
        }
    }

    @Override
    public void unfollowUser(UUID followerId, UUID followingId) {
        followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
        // Also remove friend relationship if exists
        friendRepository.findFriendship(followerId, followingId)
                .ifPresent(friendRepository::delete);
    }
}

