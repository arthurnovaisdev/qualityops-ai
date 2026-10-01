package com.arthurnovaisdev.qualityops.dto.request;

import com.arthurnovaisdev.qualityops.enums.ComplaintStatus;
import jakarta.validation.constraints.NotNull;

public record ComplaintStatusRequestDTO(

        @NotNull
        ComplaintStatus status

) {
}
