package com.arthurnovaisdev.qualityops.agent.orchestrator;

import com.arthurnovaisdev.qualityops.agent.prompt.AgentSystemPrompt;
import com.arthurnovaisdev.qualityops.dto.response.ComplaintContextResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.agent.AgentAnalysisResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.agent.AgentSuggestionDraftDTO;
import com.arthurnovaisdev.qualityops.enums.AgentExecutionStatus;
import com.arthurnovaisdev.qualityops.enums.AgentOperationType;
import com.arthurnovaisdev.qualityops.service.AgentAnalysisValidator;
import com.arthurnovaisdev.qualityops.service.AgentAuditService;
import com.arthurnovaisdev.qualityops.service.AgentContextService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
public class AgentOrchestrator {

    private final ChatClient chatClient;
    private final AgentContextService agentContextService;
    private final AgentAnalysisValidator agentAnalysisValidator;
    private final ObjectMapper objectMapper;
    private final AgentAuditService agentAuditService;

    public AgentOrchestrator(
            ChatClient.Builder chatClientBuilder,
            AgentContextService agentContextService,
            AgentAnalysisValidator agentAnalysisValidator,
            AgentAuditService agentAuditService,
            ObjectMapper objectMapper
    ) {
        this.chatClient = chatClientBuilder.build();
        this.agentContextService = agentContextService;
        this.agentAnalysisValidator = agentAnalysisValidator;
        this.agentAuditService = agentAuditService;
        this.objectMapper = objectMapper;
    }

    public AgentAnalysisResponseDTO investigate(
            UUID complaintId,
            String userMessage
    ) {

        long start = System.nanoTime();

        try {

            ComplaintContextResponseDTO context =
                    agentContextService.getComplaintContext(
                            complaintId
                    );

            String contextJson =
                    objectMapper
                            .valueToTree(context)
                            .toString();

            String request = """
                CONTEXTO AUTORITATIVO:
                %s

                SOLICITAÇÃO:
                %s

                Retorne exclusivamente JSON válido contendo:

                {
                  "hypotheses": [],
                  "missingInformation": [],
                  "nextSteps": []
                }

                Máximo de 3 itens por lista.
                Cada item deve ser curto e objetivo.
                Não use Markdown.
                Não escreva texto fora do JSON.
                """.formatted(
                    contextJson,
                    userMessage
            );

            AgentAnalysisResponseDTO analysis =
                    chatClient
                            .prompt()
                            .system(
                                    AgentSystemPrompt.SYSTEM_PROMPT
                            )
                            .user(request)
                            .call()
                            .entity(
                                    AgentAnalysisResponseDTO.class
                            );

            AgentAnalysisResponseDTO validated =
                    agentAnalysisValidator.validate(
                            analysis,
                            context
                    );

            long durationMs =
                    (System.nanoTime() - start)
                            / 1_000_000;

            agentAuditService.register(
                    complaintId,
                    AgentOperationType.ANALYSIS,
                    AgentExecutionStatus.SUCCESS,
                    userMessage,
                    objectMapper
                            .valueToTree(validated)
                            .toString(),
                    durationMs
            );

            return validated;

        } catch (Exception ex) {

            long durationMs =
                    (System.nanoTime() - start)
                            / 1_000_000;

            agentAuditService.register(
                    complaintId,
                    AgentOperationType.ANALYSIS,
                    AgentExecutionStatus.FAILED,
                    userMessage,
                    ex.getMessage(),
                    durationMs
            );

            throw ex;
        }
    }

    public AgentSuggestionDraftDTO generateSuggestion(
            ComplaintContextResponseDTO context,
            String userMessage
    ) {

        String contextJson =
                objectMapper
                        .valueToTree(context)
                        .toString();

        String request = """
            CONTEXTO AUTORITATIVO:
            %s

            SOLICITAÇÃO:
            %s

            Gere uma única sugestão de investigação para revisão humana.

            Regras:
            - Não altere a causa raiz confirmada.
            - Não repita ações corretivas já concluídas.
            - Não afirme fatos que não estejam no contexto.
            - Não diga que uma hipótese está confirmada.
            - A sugestão deve ser objetiva e ter no máximo 500 caracteres.

            Retorne exclusivamente:

            {
              "suggestion": "..."
            }
            """.formatted(
                contextJson,
                userMessage
        );

        return chatClient
                .prompt()
                .system(AgentSystemPrompt.SYSTEM_PROMPT)
                .user(request)
                .call()
                .entity(AgentSuggestionDraftDTO.class);
    }
}