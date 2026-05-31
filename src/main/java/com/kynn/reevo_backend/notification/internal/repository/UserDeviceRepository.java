package com.kynn.reevo_backend.notification.internal.repository;

import com.kynn.reevo_backend.notification.internal.domain.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, UUID> {
    List<UserDevice> findByUserId(UUID userId);
    Optional<UserDevice> findByFcmToken(String fcmToken);
    void deleteByFcmToken(String fcmToken);
    void deleteByUserId(UUID userId);
}
