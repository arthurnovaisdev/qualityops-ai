package com.arthurnovaisdev.qualityops.dto.response;

import com.arthurnovaisdev.qualityops.enums.ComplaintStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ComplaintResponseDTO(

        UUID id,
        String title,
        String description,

        UUID customerId,
        String customerName,

        UUID productId,
        String productName,

        UUID lotId,
        String lotCode,

        UUID createdById,
        String createdByName,

        ComplaintStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
