package com.arthurnovaisdev.qualityops.controller;

import com.arthurnovaisdev.qualityops.dto.response.agent.AgentExecutionResponseDTO;
import com.arthurnovaisdev.qualityops.service.AgentAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/agent-executions")
@RequiredArgsConstructor
public class AgentAuditController {

    private final AgentAuditService agentAuditService;

    @GetMapping
    public ResponseEntity<List<AgentExecutionResponseDTO>> findRecent() {

        return ResponseEntity.ok(
                agentAuditService.findRecent()
        );
    }
}