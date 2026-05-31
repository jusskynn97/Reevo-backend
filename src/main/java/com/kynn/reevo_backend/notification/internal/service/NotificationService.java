package com.kynn.reevo_backend.notification.internal.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
import com.kynn.reevo_backend.notification.api.NotificationFacade;
import com.kynn.reevo_backend.notification.internal.domain.Notification;
import com.kynn.reevo_backend.notification.internal.domain.UserDevice;
import com.kynn.reevo_backend.notification.internal.repository.NotificationRepository;
import com.kynn.reevo_backend.notification.internal.repository.UserDeviceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements NotificationFacade {

    private final NotificationRepository notificationRepository;
    private final UserDeviceRepository userDeviceRepository;

    @Override
    @Transactional
    public void sendNotification(UUID userId, Notification.NotificationType type, String title, String message, String metadata) {
        createNotification(userId, type, title, message, metadata);
    }

    @Transactional
    public void createNotification(UUID userId, Notification.NotificationType type, String title, String message, String metadata) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setMetadata(metadata);
        notification.setIsRead(false);

        notificationRepository.save(notification);

        // Send push notification via FCM
        sendPushNotification(userId, title, message, metadata);
    }

    public List<Notification> getNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void markAsRead(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            n.setReadAt(LocalDateTime.now());
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void registerDevice(UUID userId, String fcmToken, UserDevice.DeviceType deviceType) {
        userDeviceRepository.findByFcmToken(fcmToken)
                .ifPresentOrElse(
                        device -> {
                            device.setUserId(userId);
                            device.setDeviceType(deviceType);
                            userDeviceRepository.save(device);
                        },
                        () -> {
                            UserDevice device = new UserDevice();
                            device.setUserId(userId);
                            device.setFcmToken(fcmToken);
                            device.setDeviceType(deviceType);
                            userDeviceRepository.save(device);
                        }
                );
    }

    @Transactional
    public void unregisterDevice(UUID userId, String fcmToken) {
        userDeviceRepository.findByFcmToken(fcmToken)
                .ifPresent(device -> {
                    if (device.getUserId().equals(userId)) {
                        userDeviceRepository.delete(device);
                        log.info("Unregistered device for user {} with FCM token {}", userId, fcmToken);
                    }
                });
    }

    @Transactional
    public void unregisterAllDevicesForUser(UUID userId) {
        userDeviceRepository.deleteByUserId(userId);
        log.info("Unregistered all devices for user {}", userId);
    }

    private void sendPushNotification(UUID userId, String title, String body, String metadata) {
        List<UserDevice> devices = userDeviceRepository.findByUserId(userId);
        if (devices.isEmpty()) {
            log.info("No devices registered for user {}", userId);
            return;
        }

        List<String> tokens = devices.stream()
                .map(UserDevice::getFcmToken)
                .collect(Collectors.toList());

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("click_action", "FLUTTER_NOTIFICATION_CLICK")
                .putData("metadata", metadata != null ? metadata : "")
                .addAllTokens(tokens)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            log.info("Successfully sent {} push notifications to user {}", response.getSuccessCount(), userId);
            
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        // The token is no longer valid, we should remove it
                        String invalidToken = tokens.get(i);
                        userDeviceRepository.deleteByFcmToken(invalidToken);
                        log.warn("Removed invalid FCM token: {}", invalidToken);
                    }
                }
            }
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send push notifications to user {}", userId, e);
        }
    }
}
