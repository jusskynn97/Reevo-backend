package com.kynn.reevo_backend.notification;

import com.kynn.reevo_backend.common.api.ApiResponse;
import com.kynn.reevo_backend.common.api.CurrentUserId;
import com.kynn.reevo_backend.notification.internal.domain.Notification;
import com.kynn.reevo_backend.notification.internal.domain.UserDevice;
import com.kynn.reevo_backend.notification.internal.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<Notification>> getNotifications(@CurrentUserId UUID userId) {
        return ApiResponse.ok(notificationService.getNotifications(userId));
    }

    @PostMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ApiResponse.ok(null);
    }

    @PostMapping("/register-device")
    public ApiResponse<Void> registerDevice(
            @CurrentUserId UUID userId,
            @RequestParam String fcmToken,
            @RequestParam UserDevice.DeviceType deviceType) {
        notificationService.registerDevice(userId, fcmToken, deviceType);
        return ApiResponse.ok(null);
    }

    @PostMapping("/unregister-device")
    public ApiResponse<Void> unregisterDevice(
            @CurrentUserId UUID userId,
            @RequestParam String fcmToken) {
        notificationService.unregisterDevice(userId, fcmToken);
        return ApiResponse.ok(null);
    }

    @PostMapping("/unregister-all-devices")
    public ApiResponse<Void> unregisterAllDevices(@CurrentUserId UUID userId) {
        notificationService.unregisterAllDevicesForUser(userId);
        return ApiResponse.ok(null);
    }
}
