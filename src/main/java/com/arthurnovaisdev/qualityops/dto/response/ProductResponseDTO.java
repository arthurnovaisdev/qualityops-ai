package com.arthurnovaisdev.qualityops.dto.response;

import java.util.UUID;

public record ProductResponseDTO(

        UUID id,
        String name,
        String code,
        String description

) {
}
