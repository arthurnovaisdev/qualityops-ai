package com.arthurnovaisdev.qualityops.controller;

import com.arthurnovaisdev.qualityops.dto.request.ComplaintRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.ComplaintResponseDTO;
import com.arthurnovaisdev.qualityops.dto.request.ComplaintStatusRequestDTO;
import com.arthurnovaisdev.qualityops.service.ComplaintService;
import com.arthurnovaisdev.qualityops.service.ComplaintVectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;
    private final ComplaintVectorService complaintVectorService;

    @PostMapping
    public ResponseEntity<ComplaintResponseDTO> create(
            @Valid @RequestBody ComplaintRequestDTO dto,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        complaintService.create(
                                dto,
                                authentication.getName()
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<ComplaintResponseDTO>> findAll() {
        return ResponseEntity.ok(
                complaintService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintResponseDTO> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                complaintService.findById(id)
        );
    }

    @GetMapping("/{id}/similar")
    public ResponseEntity<List<ComplaintResponseDTO>> findSimilar(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                complaintVectorService
                        .searchSimilarComplaints(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComplaintResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody ComplaintRequestDTO dto
    ) {
        return ResponseEntity.ok(
                complaintService.update(id, dto)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ComplaintResponseDTO> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ComplaintStatusRequestDTO dto
    ) {
        return ResponseEntity.ok(
                complaintService.updateStatus(
                        id,
                        dto.status()
                )
        );
    }
}