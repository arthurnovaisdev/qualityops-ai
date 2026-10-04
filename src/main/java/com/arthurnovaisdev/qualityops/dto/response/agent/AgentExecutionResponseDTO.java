package com.arthurnovaisdev.qualityops.dto.response.agent;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgentExecutionResponseDTO(
        UUID id,
        UUID complaintId,
        String complaintTitle,
        UUID requestedById,
        String requestedByName,
        String operation,
        String status,
        String userMessage,
        String output,
        String model,
        Long durationMs,
        LocalDateTime createdAt
) {
}