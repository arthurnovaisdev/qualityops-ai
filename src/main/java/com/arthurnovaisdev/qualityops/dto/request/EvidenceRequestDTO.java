package com.arthurnovaisdev.qualityops.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record EvidenceRequestDTO(

        @NotNull
        UUID complaintId,

        @NotBlank
        @Size(max = 150)
        String title,

        @Size(max = 2000)
        String description,

        @Size(max = 500)
        String fileUrl

) {
}