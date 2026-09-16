package com.arthurnovaisdev.qualityops.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequestDTO(
        @NotNull
        @Email
        String email,

        @NotBlank
        String password
) {
}
