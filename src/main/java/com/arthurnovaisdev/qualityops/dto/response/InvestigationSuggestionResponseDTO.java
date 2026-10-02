package com.arthurnovaisdev.qualityops.dto.response;

import com.arthurnovaisdev.qualityops.enums.SuggestionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record InvestigationSuggestionResponseDTO(

        UUID id,

        UUID complaintId,
        String complaintTitle,

        String suggestion,
        SuggestionStatus status,

        LocalDateTime createdAt

) {
}
