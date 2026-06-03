
package com.kynn.reevo_backend.watchtogether.api.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoomRequest {
    @NotBlank
    private String name;
    private String description;
    private String videoId;
    private String videoUrl;
    private String thumbnailUrl;
    private String privacy;
    private Integer maxUsers;
}

