package com.arthurnovaisdev.qualityops.dto.response;

import com.arthurnovaisdev.qualityops.enums.CorrectiveActionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CorrectiveActionResponseDTO(

        UUID id,

        UUID investigationId,

        String description,

        UUID responsibleId,
        String responsibleName,

        LocalDate dueDate,
        CorrectiveActionStatus status,

        UUID createdById,
        String createdByName,

        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime completedAt

) {
}
