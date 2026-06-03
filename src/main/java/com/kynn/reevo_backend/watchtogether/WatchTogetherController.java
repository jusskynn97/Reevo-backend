
package com.kynn.reevo_backend.watchtogether;

import com.kynn.reevo_backend.common.api.ApiResponse;
import com.kynn.reevo_backend.common.api.CurrentUserId;
import com.kynn.reevo_backend.watchtogether.api.dto.*;
import com.kynn.reevo_backend.watchtogether.internal.service.WatchTogetherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/watch-together")
@RequiredArgsConstructor
public class WatchTogetherController {

    private final WatchTogetherService watchTogetherService;

    @PostMapping("/rooms")
    public ApiResponse<WatchRoomResponse> createRoom(
            @CurrentUserId UUID userId,
            @Valid @RequestBody CreateRoomRequest request
    ) {
        WatchRoomResponse room = watchTogetherService.createRoom(userId, request);
        return ApiResponse.ok(room);
    }

    @GetMapping("/rooms/public")
    public ApiResponse<List<WatchRoomResponse>> getPublicRooms() {
        List<WatchRoomResponse> rooms = watchTogetherService.getPublicRooms();
        return ApiResponse.ok(rooms);
    }

    @GetMapping("/rooms/{roomId}")
    public ApiResponse<WatchRoomResponse> getRoom(@PathVariable UUID roomId) {
        WatchRoomResponse room = watchTogetherService.getRoom(roomId);
        return ApiResponse.ok(room);
    }

    @PostMapping("/rooms/{roomId}/join")
    public ApiResponse<Void> joinRoom(
            @CurrentUserId UUID userId,
            @PathVariable UUID roomId
    ) {
        watchTogetherService.joinRoom(roomId, userId);
        return ApiResponse.ok(null);
    }

    @PostMapping("/rooms/{roomId}/leave")
    public ApiResponse<Void> leaveRoom(
            @CurrentUserId UUID userId,
            @PathVariable UUID roomId
    ) {
        watchTogetherService.leaveRoom(roomId, userId);
        return ApiResponse.ok(null);
    }

    @GetMapping("/rooms/{roomId}/participants")
    public ApiResponse<List<RoomParticipantResponse>> getRoomParticipants(@PathVariable UUID roomId) {
        List<RoomParticipantResponse> participants = watchTogetherService.getRoomParticipants(roomId);
        return ApiResponse.ok(participants);
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ApiResponse<RoomMessageResponse> sendMessage(
            @CurrentUserId UUID userId,
            @PathVariable UUID roomId,
            @Valid @RequestBody SendMessageRequest request
    ) {
        RoomMessageResponse message = watchTogetherService.sendMessage(roomId, userId, request);
        return ApiResponse.ok(message);
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ApiResponse<List<RoomMessageResponse>> getRoomMessages(@PathVariable UUID roomId) {
        List<RoomMessageResponse> messages = watchTogetherService.getRoomMessages(roomId);
        return ApiResponse.ok(messages);
    }
}
