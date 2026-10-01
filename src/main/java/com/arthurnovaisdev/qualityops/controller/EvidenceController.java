package com.arthurnovaisdev.qualityops.controller;

import com.arthurnovaisdev.qualityops.dto.request.EvidenceRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.EvidenceResponseDTO;
import com.arthurnovaisdev.qualityops.service.EvidenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/evidences")
@RequiredArgsConstructor
public class EvidenceController {

    private final EvidenceService evidenceService;

    @PostMapping
    public ResponseEntity<EvidenceResponseDTO> create(
            @Valid @RequestBody EvidenceRequestDTO dto,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        evidenceService.create(
                                dto,
                                authentication.getName()
                        )
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvidenceResponseDTO> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                evidenceService.findById(id)
        );
    }

    @GetMapping("/complaint/{complaintId}")
    public ResponseEntity<List<EvidenceResponseDTO>> findByComplaint(
            @PathVariable UUID complaintId
    ) {
        return ResponseEntity.ok(
                evidenceService.findByComplaint(complaintId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id
    ) {
        evidenceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
