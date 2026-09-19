package com.arthurnovaisdev.qualityops.controller;

import com.arthurnovaisdev.qualityops.dto.request.LotRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.LotResponseDTO;
import com.arthurnovaisdev.qualityops.service.LotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lots")
@RequiredArgsConstructor
public class LotController {

    private final LotService lotService;

    @PostMapping
    public ResponseEntity<LotResponseDTO> create(
            @Valid @RequestBody LotRequestDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lotService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<LotResponseDTO>> findAll() {
        return ResponseEntity.ok(lotService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LotResponseDTO> findById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(lotService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LotResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody LotRequestDTO dto
    ) {
        return ResponseEntity.ok(lotService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        lotService.delete(id);
        return ResponseEntity.noContent().build();
    }
}