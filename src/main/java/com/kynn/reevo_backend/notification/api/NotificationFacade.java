package com.kynn.reevo_backend.notification.api;

import com.kynn.reevo_backend.notification.internal.domain.Notification;

import java.util.UUID;

public interface NotificationFacade {
    void sendNotification(UUID userId, Notification.NotificationType type, String title, String message, String metadata);
}
