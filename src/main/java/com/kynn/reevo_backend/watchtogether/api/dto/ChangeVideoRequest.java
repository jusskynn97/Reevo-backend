
package com.kynn.reevo_backend.watchtogether.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeVideoRequest {
    @NotNull
    private String videoId;

    @NotBlank
    private String videoUrl;

    private String thumbnailUrl;
}
