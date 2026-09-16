package com.arthurnovaisdev.qualityops.dto.response;

import com.arthurnovaisdev.qualityops.enums.Role;

import java.util.UUID;

public record LoginResponseDTO(
        UUID id,
        String name,
        String email,
        Role role
) {
}
