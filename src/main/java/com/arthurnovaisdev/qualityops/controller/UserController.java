package com.arthurnovaisdev.qualityops.controller;

import com.arthurnovaisdev.qualityops.dto.UserStatusDTO;
import com.arthurnovaisdev.qualityops.dto.request.UserRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.UserResponseDTO;
import com.arthurnovaisdev.qualityops.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<UserResponseDTO> updateStatus(@PathVariable UUID id, @RequestBody UserStatusDTO dto) {
        return ResponseEntity.ok(
                userService.updateStatus(id, dto.active())
        );
    }
}
