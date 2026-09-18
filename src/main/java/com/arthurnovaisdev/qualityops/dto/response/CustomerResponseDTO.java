package com.arthurnovaisdev.qualityops.dto.response;

import java.util.UUID;

public record CustomerResponseDTO(
        UUID id,
        String name,
        String document,
        String contact
) {
}
