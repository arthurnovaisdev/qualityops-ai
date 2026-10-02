package com.arthurnovaisdev.qualityops.dto.response.agent;

public record AgentInvestigationContextDTO(
        String analysis,
        String confirmedRootCause,
        String status,
        String createdByName
) {
}