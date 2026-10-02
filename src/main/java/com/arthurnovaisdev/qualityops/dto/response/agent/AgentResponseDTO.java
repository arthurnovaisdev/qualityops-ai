package com.arthurnovaisdev.qualityops.dto.response.agent;

import java.util.UUID;

public record AgentResponseDTO(
        UUID complaintId,
        AgentAnalysisResponseDTO analysis
) {
}