package com.arthurnovaisdev.qualityops.controller;

import com.arthurnovaisdev.qualityops.agent.orchestrator.AgentOrchestrator;
import com.arthurnovaisdev.qualityops.dto.request.agent.AgentRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.InvestigationSuggestionResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.agent.AgentAnalysisResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.agent.AgentResponseDTO;
import com.arthurnovaisdev.qualityops.exception.AgentRateLimitExceededException;
import com.arthurnovaisdev.qualityops.service.AgentInputSecurityService;
import com.arthurnovaisdev.qualityops.service.AgentRateLimitService;
import com.arthurnovaisdev.qualityops.service.AgentSuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentOrchestrator agentOrchestrator;
    private final AgentSuggestionService agentSuggestionService;
    private final AgentRateLimitService agentRateLimitService;
    private final AgentInputSecurityService agentInputSecurityService;

    @PostMapping("/complaints/{complaintId}/investigate")
    public ResponseEntity<AgentResponseDTO> investigate(
            @PathVariable UUID complaintId,
            @Valid @RequestBody AgentRequestDTO request
    ) {

        if (!agentRateLimitService.tryConsume()) {
            throw new AgentRateLimitExceededException(
                    "Limite de requisições da IA excedido. Aguarde antes de tentar novamente."
            );
        }

        agentInputSecurityService.validate(request.message());

        AgentAnalysisResponseDTO analysis =
                agentOrchestrator.investigate(
                        complaintId,
                        request.message()
                );

        return ResponseEntity.ok(
                new AgentResponseDTO(
                        complaintId,
                        analysis
                )
        );
    }

    @PostMapping("/complaints/{complaintId}/suggestions")
    public ResponseEntity<InvestigationSuggestionResponseDTO> createSuggestion(
            @PathVariable UUID complaintId,
            @Valid @RequestBody AgentRequestDTO request
    ) {

        if (!agentRateLimitService.tryConsume()) {
            throw new AgentRateLimitExceededException(
                    "Limite de requisições da IA excedido. Aguarde antes de tentar novamente."
            );
        }

        agentInputSecurityService.validate(request.message());

        InvestigationSuggestionResponseDTO suggestion =
                agentSuggestionService.createSuggestion(
                        complaintId,
                        request.message()
                );

        return ResponseEntity.ok(suggestion);
    }
}