package com.arthurnovaisdev.qualityops.dto.response.agent;

import java.util.List;

public record AgentAnalysisResponseDTO(
        List<String> hypotheses,
        List<String> missingInformation,
        List<String> nextSteps
) {
}