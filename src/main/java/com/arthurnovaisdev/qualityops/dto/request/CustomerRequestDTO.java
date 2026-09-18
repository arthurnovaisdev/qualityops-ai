package com.arthurnovaisdev.qualityops.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequestDTO (
        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 30)
        String document,

        @Size(max = 150)
        String contact
) {
}
