package com.arthurnovaisdev.qualityops.dto.request;

import com.arthurnovaisdev.qualityops.enums.InvestigationStatus;
import jakarta.validation.constraints.NotNull;

public record InvestigationStatusRequestDTO(

        @NotNull
        InvestigationStatus status

) {
}
