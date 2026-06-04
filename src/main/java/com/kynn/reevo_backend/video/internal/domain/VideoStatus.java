package com.kynn.reevo_backend.video.internal.domain;

public enum VideoStatus {
    PENDING,        // Đang chờ xử lý
    UPLOADING,      // Đang upload lên Cloudinary
    PROCESSING,     // Cloudinary đang xử lý video
    READY,          // Sẵn sàng phát
    FAILED
}