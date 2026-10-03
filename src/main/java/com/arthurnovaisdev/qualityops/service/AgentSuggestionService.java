package com.arthurnovaisdev.qualityops.service;

import com.arthurnovaisdev.qualityops.agent.orchestrator.AgentOrchestrator;
import com.arthurnovaisdev.qualityops.dto.request.InvestigationSuggestionRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.ComplaintContextResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.InvestigationSuggestionResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.agent.AgentSuggestionDraftDTO;
import com.arthurnovaisdev.qualityops.enums.AgentExecutionStatus;
import com.arthurnovaisdev.qualityops.enums.AgentOperationType;
import com.arthurnovaisdev.qualityops.exception.AgentGenerationException;
import com.arthurnovaisdev.qualityops.exception.AgentSuggestionConflictException;
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
    private final AgentAuditService agentAuditService;

    public InvestigationSuggestionResponseDTO createSuggestion(
            UUID complaintId,
            String userMessage
    ) {

        long start = System.nanoTime();

        String generatedSuggestion = null;

        try {

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

            generatedSuggestion =
                    draft.suggestion().trim();

            if (generatedSuggestion.length() > 500) {

                throw new AgentGenerationException(
                        "A sugestão gerada ultrapassou o limite permitido."
                );
            }

            List<InvestigationSuggestionResponseDTO> existingSuggestions =
                    investigationSuggestionService.findByComplaint(
                            complaintId
                    );

            agentSuggestionValidator.validate(
                    generatedSuggestion,
                    context,
                    existingSuggestions
            );

            InvestigationSuggestionRequestDTO request =
                    new InvestigationSuggestionRequestDTO(
                            complaintId,
                            generatedSuggestion
                    );

            InvestigationSuggestionResponseDTO response =
                    investigationSuggestionService.create(
                            request
                    );

            long durationMs =
                    (System.nanoTime() - start)
                            / 1_000_000;

            agentAuditService.register(
                    complaintId,
                    AgentOperationType.SUGGESTION,
                    AgentExecutionStatus.SUCCESS,
                    userMessage,
                    generatedSuggestion,
                    durationMs
            );

            return response;

        } catch (AgentSuggestionConflictException ex) {

            long durationMs =
                    (System.nanoTime() - start)
                            / 1_000_000;

            agentAuditService.register(
                    complaintId,
                    AgentOperationType.SUGGESTION,
                    AgentExecutionStatus.BLOCKED,
                    userMessage,
                    generatedSuggestion != null
                            ? generatedSuggestion
                            : ex.getMessage(),
                    durationMs
            );

            throw ex;

        } catch (Exception ex) {

            long durationMs =
                    (System.nanoTime() - start)
                            / 1_000_000;

            agentAuditService.register(
                    complaintId,
                    AgentOperationType.SUGGESTION,
                    AgentExecutionStatus.FAILED,
                    userMessage,
                    ex.getMessage(),
                    durationMs
            );

            throw ex;
        }
    }
}