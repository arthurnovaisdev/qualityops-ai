package com.arthurnovaisdev.qualityops.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record InvestigationSuggestionRequestDTO(

        @NotNull
        UUID complaintId,

        @NotBlank
        @Size(max = 4000)
        String suggestion

) {
}
