package com.arthurnovaisdev.qualityops.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record InvestigationRequestDTO(

        @NotNull
        UUID complaintId,

        @Size(max = 3000)
        String analysis,

        @Size(max = 2000)
        String rootCause

) {
}
