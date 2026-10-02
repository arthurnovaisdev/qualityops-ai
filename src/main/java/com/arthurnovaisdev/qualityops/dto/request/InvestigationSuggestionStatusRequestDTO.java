package com.arthurnovaisdev.qualityops.dto.request;

import com.arthurnovaisdev.qualityops.enums.SuggestionStatus;
import jakarta.validation.constraints.NotNull;

public record InvestigationSuggestionStatusRequestDTO(

        @NotNull
        SuggestionStatus status

) {
}