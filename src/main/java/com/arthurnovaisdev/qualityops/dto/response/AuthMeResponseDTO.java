package com.arthurnovaisdev.qualityops.dto.response;

import java.util.UUID;

public record AuthMeResponseDTO(
        UUID id,
        String name,
        String email,
        String role,
        boolean active
) {
}