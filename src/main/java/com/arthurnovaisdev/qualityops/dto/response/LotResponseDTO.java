package com.arthurnovaisdev.qualityops.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record LotResponseDTO(

        UUID id,
        String code,
        UUID productId,
        String productName,
        LocalDate manufacturingDate,
        LocalDate expirationDate

) {
}
