
package com.kynn.reevo_backend.watchtogether.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoSyncEvent {
    private String type; // PLAY, PAUSE, SEEK
    private Long position; // in milliseconds
    private Boolean playing;
    private Long timestamp;
    private String userId;
    private String username;
}
