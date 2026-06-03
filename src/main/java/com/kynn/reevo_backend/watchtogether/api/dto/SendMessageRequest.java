
package com.kynn.reevo_backend.watchtogether.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageRequest {
    @NotBlank
    private String content;
    private String imageUrl;
}

