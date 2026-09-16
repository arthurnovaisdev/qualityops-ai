package com.arthurnovaisdev.qualityops.dto.request;

import com.arthurnovaisdev.qualityops.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size
        String password,

        @NotNull
        Role role
) {
}
