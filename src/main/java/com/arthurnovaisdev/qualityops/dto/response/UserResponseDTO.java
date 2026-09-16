package com.arthurnovaisdev.qualityops.dto.response;

import com.arthurnovaisdev.qualityops.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO (

    UUID id,
    String name,
    String email,
    Role role,
    boolean active,
    LocalDateTime createdAt
) {
}
