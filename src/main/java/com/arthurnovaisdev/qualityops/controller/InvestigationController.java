package com.arthurnovaisdev.qualityops.controller;

import com.arthurnovaisdev.qualityops.dto.request.InvestigationRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.InvestigationResponseDTO;
import com.arthurnovaisdev.qualityops.dto.request.InvestigationStatusRequestDTO;
import com.arthurnovaisdev.qualityops.service.InvestigationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/investigations")
@RequiredArgsConstructor
public class InvestigationController {

    private final InvestigationService investigationService;

    @PostMapping
    public ResponseEntity<InvestigationResponseDTO> create(
            @Valid @RequestBody InvestigationRequestDTO dto,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        investigationService.create(
                                dto,
                                authentication.getName()
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<InvestigationResponseDTO>> findAll() {
        return ResponseEntity.ok(
                investigationService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvestigationResponseDTO> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                investigationService.findById(id)
        );
    }

    @GetMapping("/complaint/{complaintId}")
    public ResponseEntity<InvestigationResponseDTO> findByComplaint(
            @PathVariable UUID complaintId
    ) {
        return ResponseEntity.ok(
                investigationService.findByComplaint(complaintId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvestigationResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody InvestigationRequestDTO dto
    ) {
        return ResponseEntity.ok(
                investigationService.update(id, dto)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<InvestigationResponseDTO> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody InvestigationStatusRequestDTO dto
    ) {
        return ResponseEntity.ok(
                investigationService.updateStatus(
                        id,
                        dto.status()
                )
        );
    }
}