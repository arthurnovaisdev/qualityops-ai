package com.arthurnovaisdev.qualityops.dto.request;

import com.arthurnovaisdev.qualityops.enums.CorrectiveActionStatus;
import jakarta.validation.constraints.NotNull;

public record CorrectiveActionStatusRequestDTO(

        @NotNull
        CorrectiveActionStatus status

) {
}
