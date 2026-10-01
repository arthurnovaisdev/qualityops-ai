package com.arthurnovaisdev.qualityops.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ComplaintRequestDTO(

        @NotBlank
        @Size(max = 150)
        String title,

        @NotBlank
        @Size(max = 3000)
        String description,

        @NotNull
        UUID customerId,

        @NotNull
        UUID productId,

        UUID lotId

) {
}