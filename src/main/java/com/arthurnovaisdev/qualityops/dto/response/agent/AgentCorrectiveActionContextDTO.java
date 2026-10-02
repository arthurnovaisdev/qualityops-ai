package com.arthurnovaisdev.qualityops.dto.response.agent;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AgentCorrectiveActionContextDTO(
        String description,
        String responsibleName,
        String status,
        boolean completed,
        LocalDate deadline,
        LocalDateTime actualCompletionDate,
        boolean effectivenessVerified
) {
}