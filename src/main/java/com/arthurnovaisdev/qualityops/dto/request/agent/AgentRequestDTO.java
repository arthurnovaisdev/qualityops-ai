package com.arthurnovaisdev.qualityops.dto.request.agent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AgentRequestDTO(

        @NotBlank
        @Size(max = 2000)
        String message

) {
}
