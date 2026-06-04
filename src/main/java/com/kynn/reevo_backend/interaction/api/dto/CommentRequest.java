package com.kynn.reevo_backend.interaction.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.UUID;

@Data
public class CommentRequest {
    @NotBlank(message = "Content of comments must not blank")
    private String content;
    private UUID parentId;
}
