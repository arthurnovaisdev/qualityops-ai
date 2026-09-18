package com.arthurnovaisdev.qualityops.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductRequestDTO(

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 80)
        String code,

        @Size(max = 500)
        String description

) {
}
