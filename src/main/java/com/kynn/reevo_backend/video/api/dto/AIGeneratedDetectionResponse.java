package com.kynn.reevo_backend.video.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AIGeneratedDetectionResponse {
    private String filename;
    private String label;
    @JsonProperty("label_vi")
    private String labelVi;
    @JsonProperty("is_ai_generated")
    private boolean isAiGenerated;
    @JsonProperty("probability_ai")
    private double probabilityAi;
    private double confidence;
    @JsonProperty("confidence_pct")
    private double confidencePct;
    @JsonProperty("threshold_used")
    private double thresholdUsed;

    // Make sure isAiGenerated has proper getter/setter for Jackson
    @JsonProperty("is_ai_generated")
    public boolean isAiGenerated() {
        return isAiGenerated;
    }

    @JsonProperty("is_ai_generated")
    public void setAiGenerated(boolean aiGenerated) {
        isAiGenerated = aiGenerated;
    }
}
