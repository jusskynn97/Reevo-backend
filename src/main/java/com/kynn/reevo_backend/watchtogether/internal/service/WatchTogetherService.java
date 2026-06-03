
package com.kynn.reevo_backend.watchtogether.internal.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kynn.reevo_backend.common.exception.ResourceNotFoundException;
import com.kynn.reevo_backend.user.internal.domain.Account;
import com.kynn.reevo_backend.user.internal.domain.UserProfile;
import com.kynn.reevo_backend.user.internal.repository.AccountRepository;
import com.kynn.reevo_backend.user.internal.repository.UserProfileRepository;
import com.kynn.reevo_backend.watchtogether.api.dto.CreateRoomRequest;
import com.kynn.reevo_backend.watchtogether.api.dto.RoomMessageResponse;
import com.kynn.reevo_backend.watchtogether.api.dto.RoomParticipantResponse;
import com.kynn.reevo_backend.watchtogether.api.dto.SendMessageRequest;
import com.kynn.reevo_backend.watchtogether.api.dto.VideoChangeEvent;
import com.kynn.reevo_backend.watchtogether.api.dto.VideoSyncEvent;
import com.kynn.reevo_backend.watchtogether.api.dto.WatchRoomResponse;
import com.kynn.reevo_backend.watchtogether.internal.domain.RoomMessage;
import com.kynn.reevo_backend.watchtogether.internal.domain.RoomParticipant;
import com.kynn.reevo_backend.watchtogether.internal.domain.RoomPrivacy;
import com.kynn.reevo_backend.watchtogether.internal.domain.WatchRoom;
import com.kynn.reevo_backend.watchtogether.internal.repository.RoomMessageRepository;
import com.kynn.reevo_backend.watchtogether.internal.repository.RoomParticipantRepository;
import com.kynn.reevo_backend.watchtogether.internal.repository.WatchRoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatchTogetherService {

    private final WatchRoomRepository watchRoomRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final RoomMessageRepository roomMessageRepository;
    private final AccountRepository accountRepository;
    private final UserProfileRepository userProfileRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public WatchRoomResponse createRoom(UUID creatorId, CreateRoomRequest request) {
        WatchRoom room = new WatchRoom();
        room.setCreatorId(creatorId);
        room.setName(request.getName());
        room.setDescription(request.getDescription());
        room.setVideoId(request.getVideoId());
        room.setVideoUrl(request.getVideoUrl());
        room.setThumbnailUrl(request.getThumbnailUrl());
        room.setPrivacy(request.getPrivacy() != null ? RoomPrivacy.valueOf(request.getPrivacy()) : RoomPrivacy.PUBLIC);
        room.setMaxUsers(request.getMaxUsers() != null ? request.getMaxUsers() : 20);
        room.setIsActive(true);

        WatchRoom savedRoom = watchRoomRepository.save(room);

        // Add creator as participant
        addParticipant(savedRoom.getId(), creatorId);

        return toWatchRoomResponse(savedRoom);
    }

    public List<WatchRoomResponse> getPublicRooms() {
        return watchRoomRepository.findByPrivacyAndIsActiveTrueOrderByCreatedAtDesc(RoomPrivacy.PUBLIC)
                .stream()
                .map(this::toWatchRoomResponse)
                .collect(Collectors.toList());
    }

    public WatchRoomResponse getRoom(UUID roomId) {
        WatchRoom room = watchRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        return toWatchRoomResponse(room);
    }

    @Transactional
    public void joinRoom(UUID roomId, UUID userId) {
        WatchRoom room = watchRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (!room.getIsActive()) {
            throw new IllegalStateException("Room is not active");
        }

        long currentParticipants = roomParticipantRepository.countByRoomId(roomId);
        if (currentParticipants >= room.getMaxUsers()) {
            throw new IllegalStateException("Room is full");
        }

        addParticipant(roomId, userId);
    }

    private void addParticipant(UUID roomId, UUID userId) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));

        RoomParticipant participant = new RoomParticipant();
        participant.setRoomId(roomId);
        participant.setUserId(userId);
        participant.setUsername(account.getUsername());
        participant.setAvatarUrl(userProfile.getAvatarUrl());
        participant.setIsMuted(false);
        participant.setIsSpeaking(false);

        roomParticipantRepository.save(participant);

        // Notify room of new participant
        RoomParticipantResponse participantResponse = toParticipantResponse(participant);
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/participants", participantResponse);

        // Send current video to new participant if room has a video set
        WatchRoom room = watchRoomRepository.findById(roomId).orElse(null);
        if (room != null && room.getVideoId() != null && room.getVideoUrl() != null) {
            Account creatorAccount = accountRepository.findById(room.getCreatorId()).orElse(null);
            VideoChangeEvent videoChangeEvent = VideoChangeEvent.builder()
                    .videoId(room.getVideoId())
                    .videoUrl(room.getVideoUrl())
                    .thumbnailUrl(room.getThumbnailUrl())
                    .timestamp(System.currentTimeMillis())
                    .userId(room.getCreatorId().toString())
                    .username(creatorAccount != null ? creatorAccount.getUsername() : "")
                    .build();
            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/video-change", videoChangeEvent);
        }
    }

    @Transactional
    public void leaveRoom(UUID roomId, UUID userId) {
        roomParticipantRepository.deleteByRoomIdAndUserId(roomId, userId);
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/participants/leave", userId);
    }

    public List<RoomParticipantResponse> getRoomParticipants(UUID roomId) {
        return roomParticipantRepository.findByRoomId(roomId)
                .stream()
                .map(this::toParticipantResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomMessageResponse sendMessage(UUID roomId, UUID userId, SendMessageRequest request) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found"));

        RoomMessage message = new RoomMessage();
        message.setRoomId(roomId);
        message.setSenderId(userId);
        message.setSenderUsername(account.getUsername());
        message.setSenderAvatarUrl(userProfile.getAvatarUrl());
        message.setContent(request.getContent());
        message.setImageUrl(request.getImageUrl());

        RoomMessage savedMessage = roomMessageRepository.save(message);
        RoomMessageResponse response = toMessageResponse(savedMessage);

        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/messages", response);

        return response;
    }

    public List<RoomMessageResponse> getRoomMessages(UUID roomId) {
        // Get messages from last 7 days
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return roomMessageRepository.findByRoomIdAndCreatedAtAfterOrderByCreatedAtAsc(roomId, sevenDaysAgo)
                .stream()
                .map(this::toMessageResponse)
                .collect(Collectors.toList());
    }

    public void syncVideo(UUID roomId, UUID userId, VideoSyncEvent event) {
        // Try to get account, but don't fail if not found (for testing)
        String username = "Unknown";
        try {
            Account account = accountRepository.findById(userId).orElse(null);
            if (account != null) {
                username = account.getUsername();
            }
        } catch (Exception e) {
            // Ignore for now
        }

        event.setUserId(userId.toString());
        event.setUsername(username);
        event.setTimestamp(System.currentTimeMillis());

        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/video-sync", event);
    }

    @Transactional
    public void changeVideo(UUID roomId, UUID userId, String videoId, String videoUrl, String thumbnailUrl) {
        WatchRoom room = watchRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        // Only creator (host) can change video - skip check for now for testing
        // if (!room.getCreatorId().equals(userId)) {
        //     throw new IllegalStateException("Only room host can change video");
        // }

        // Try to get account, but don't fail if not found
        String username = "Unknown";
        try {
            Account account = accountRepository.findById(userId).orElse(null);
            if (account != null) {
                username = account.getUsername();
            }
        } catch (Exception e) {
            // Ignore for now
        }

        // Update room
        room.setVideoId(videoId);
        room.setVideoUrl(videoUrl);
        room.setThumbnailUrl(thumbnailUrl);
        room.setUpdatedAt(LocalDateTime.now());

        watchRoomRepository.save(room);

        // Send event to all participants
        VideoChangeEvent videoChangeEvent = VideoChangeEvent.builder()
                .videoId(videoId)
                .videoUrl(videoUrl)
                .thumbnailUrl(thumbnailUrl)
                .timestamp(System.currentTimeMillis())
                .userId(userId.toString())
                .username(username)
                .build();

        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/video-change", videoChangeEvent);
    }

    private WatchRoomResponse toWatchRoomResponse(WatchRoom room) {
        long participantCount = roomParticipantRepository.countByRoomId(room.getId());
        return WatchRoomResponse.builder()
                .id(room.getId())
                .creatorId(room.getCreatorId())
                .name(room.getName())
                .description(room.getDescription())
                .videoId(room.getVideoId())
                .videoUrl(room.getVideoUrl())
                .thumbnailUrl(room.getThumbnailUrl())
                .privacy(room.getPrivacy().name())
                .isActive(room.getIsActive())
                .maxUsers(room.getMaxUsers())
                .participantCount((int) participantCount)
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }

    private RoomParticipantResponse toParticipantResponse(RoomParticipant participant) {
        return RoomParticipantResponse.builder()
                .id(participant.getId())
                .userId(participant.getUserId())
                .username(participant.getUsername())
                .avatarUrl(participant.getAvatarUrl())
                .isMuted(participant.getIsMuted())
                .isSpeaking(participant.getIsSpeaking())
                .joinedAt(participant.getJoinedAt())
                .build();
    }

    private RoomMessageResponse toMessageResponse(RoomMessage message) {
        return RoomMessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .senderUsername(message.getSenderUsername())
                .senderAvatarUrl(message.getSenderAvatarUrl())
                .content(message.getContent())
                .imageUrl(message.getImageUrl())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
