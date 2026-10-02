package com.arthurnovaisdev.qualityops.controller;

import com.arthurnovaisdev.qualityops.dto.response.ComplaintResponseDTO;
import com.arthurnovaisdev.qualityops.service.ComplaintVectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/vector")
@RequiredArgsConstructor
public class ComplaintVectorController {

    private final ComplaintVectorService complaintVectorService;

    @PostMapping("/complaints/reindex")
    public ResponseEntity<Void> reindexComplaints() {

        complaintVectorService.indexAllComplaints();

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/complaints/{complaintId}/similar")
    public ResponseEntity<List<ComplaintResponseDTO>> findSimilar(
            @PathVariable UUID complaintId
    ) {

        List<ComplaintResponseDTO> similar =
                complaintVectorService.searchSimilarComplaints(
                        complaintId
                );

        return ResponseEntity.ok(similar);
    }
}