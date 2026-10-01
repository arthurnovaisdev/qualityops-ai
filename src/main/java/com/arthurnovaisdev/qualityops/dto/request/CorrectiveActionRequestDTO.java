package com.arthurnovaisdev.qualityops.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CorrectiveActionRequestDTO(

        @NotNull
        UUID investigationId,

        @NotBlank
        @Size(max = 2000)
        String description,

        @NotNull
        UUID responsibleId,

        @NotNull
        @FutureOrPresent
        LocalDate dueDate

) {
}
