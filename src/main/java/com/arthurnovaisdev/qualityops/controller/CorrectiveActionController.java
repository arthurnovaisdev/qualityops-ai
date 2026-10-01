package com.arthurnovaisdev.qualityops.controller;

import com.arthurnovaisdev.qualityops.dto.request.CorrectiveActionRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.CorrectiveActionResponseDTO;
import com.arthurnovaisdev.qualityops.dto.request.CorrectiveActionStatusRequestDTO;
import com.arthurnovaisdev.qualityops.service.CorrectiveActionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/corrective-actions")
@RequiredArgsConstructor
public class CorrectiveActionController {

    private final CorrectiveActionService correctiveActionService;

    @PostMapping
    public ResponseEntity<CorrectiveActionResponseDTO> create(
            @Valid @RequestBody CorrectiveActionRequestDTO dto,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        correctiveActionService.create(
                                dto,
                                authentication.getName()
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<CorrectiveActionResponseDTO>> findAll() {
        return ResponseEntity.ok(
                correctiveActionService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CorrectiveActionResponseDTO> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                correctiveActionService.findById(id)
        );
    }

    @GetMapping("/investigation/{investigationId}")
    public ResponseEntity<List<CorrectiveActionResponseDTO>> findByInvestigation(
            @PathVariable UUID investigationId
    ) {
        return ResponseEntity.ok(
                correctiveActionService.findByInvestigation(investigationId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CorrectiveActionResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody CorrectiveActionRequestDTO dto
    ) {
        return ResponseEntity.ok(
                correctiveActionService.update(id, dto)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CorrectiveActionResponseDTO> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody CorrectiveActionStatusRequestDTO dto
    ) {
        return ResponseEntity.ok(
                correctiveActionService.updateStatus(
                        id,
                        dto.status()
                )
        );
    }
}