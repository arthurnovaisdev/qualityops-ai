package com.arthurnovaisdev.qualityops.service;

import com.arthurnovaisdev.qualityops.agent.orchestrator.AgentOrchestrator;
import com.arthurnovaisdev.qualityops.dto.request.InvestigationSuggestionRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.ComplaintContextResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.InvestigationSuggestionResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.agent.AgentSuggestionDraftDTO;
import com.arthurnovaisdev.qualityops.exception.AgentGenerationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgentSuggestionService {

    private final AgentOrchestrator agentOrchestrator;
    private final AgentContextService agentContextService;
    private final AgentSuggestionValidator agentSuggestionValidator;
    private final InvestigationSuggestionService investigationSuggestionService;

    public InvestigationSuggestionResponseDTO createSuggestion(
            UUID complaintId,
            String userMessage
    ) {

        ComplaintContextResponseDTO context =
                agentContextService.getComplaintContext(
                        complaintId
                );

        AgentSuggestionDraftDTO draft =
                agentOrchestrator.generateSuggestion(
                        context,
                        userMessage
                );

        if (draft == null
                || draft.suggestion() == null
                || draft.suggestion().isBlank()) {

            throw new AgentGenerationException(
                    "A IA não gerou uma sugestão válida."
            );
        }

        String suggestion =
                draft.suggestion().trim();

        if (suggestion.length() > 500) {
            throw new AgentGenerationException(
                    "A sugestão gerada ultrapassou o limite permitido."
            );
        }

        List<InvestigationSuggestionResponseDTO> existingSuggestions =
                investigationSuggestionService.findByComplaint(
                        complaintId
                );

        agentSuggestionValidator.validate(
                suggestion,
                context,
                existingSuggestions
        );

        InvestigationSuggestionRequestDTO request =
                new InvestigationSuggestionRequestDTO(
                        complaintId,
                        suggestion
                );

        return investigationSuggestionService.create(
                request
        );
    }
}