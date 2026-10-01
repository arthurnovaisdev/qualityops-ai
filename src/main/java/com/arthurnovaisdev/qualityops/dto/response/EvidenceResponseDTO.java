package com.arthurnovaisdev.qualityops.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record EvidenceResponseDTO(

        UUID id,

        UUID complaintId,
        String complaintTitle,

        String title,
        String description,
        String fileUrl,

        UUID createdById,
        String createdByName,

        LocalDateTime createdAt

) {
}
