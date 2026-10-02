package com.arthurnovaisdev.qualityops.controller;

import com.arthurnovaisdev.qualityops.dto.request.InvestigationSuggestionStatusRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.InvestigationSuggestionResponseDTO;
import com.arthurnovaisdev.qualityops.service.InvestigationSuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/investigation-suggestions")
@RequiredArgsConstructor
public class InvestigationSuggestionController {

    private final InvestigationSuggestionService investigationSuggestionService;

    @GetMapping("/complaint/{complaintId}")
    public ResponseEntity<List<InvestigationSuggestionResponseDTO>> findByComplaint(
            @PathVariable UUID complaintId
    ) {

        List<InvestigationSuggestionResponseDTO> suggestions =
                investigationSuggestionService.findByComplaint(
                        complaintId
                );

        return ResponseEntity.ok(suggestions);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<InvestigationSuggestionResponseDTO> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody InvestigationSuggestionStatusRequestDTO request
    ) {

        InvestigationSuggestionResponseDTO response =
                investigationSuggestionService.updateStatus(
                        id,
                        request.status()
                );

        return ResponseEntity.ok(response);
    }
}