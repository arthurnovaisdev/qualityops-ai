package com.arthurnovaisdev.qualityops.dto.response;

import com.arthurnovaisdev.qualityops.enums.InvestigationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record InvestigationResponseDTO(

        UUID id,

        UUID complaintId,
        String complaintTitle,

        String analysis,
        String rootCause,

        InvestigationStatus status,

        UUID createdById,
        String createdByName,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
