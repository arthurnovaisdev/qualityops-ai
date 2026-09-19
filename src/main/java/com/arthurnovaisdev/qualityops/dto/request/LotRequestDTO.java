package com.arthurnovaisdev.qualityops.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record LotRequestDTO(

        @NotBlank
        @Size(max = 80)
        String code,

        @NotNull
        UUID productId,

        LocalDate manufacturingDate,

        LocalDate expirationDate

) {
}
