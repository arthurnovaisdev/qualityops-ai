package com.arthurnovaisdev.qualityops.agent.orchestrator;

import com.arthurnovaisdev.qualityops.agent.prompt.AgentSystemPrompt;
import com.arthurnovaisdev.qualityops.dto.response.ComplaintContextResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.agent.AgentAnalysisResponseDTO;
import com.arthurnovaisdev.qualityops.service.AgentAnalysisValidator;
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

    public AgentOrchestrator(
            ChatClient.Builder chatClientBuilder,
            AgentContextService agentContextService,
            AgentAnalysisValidator agentAnalysisValidator,
            ObjectMapper objectMapper
    ) {
        this.chatClient = chatClientBuilder.build();
        this.agentContextService = agentContextService;
        this.agentAnalysisValidator = agentAnalysisValidator;
        this.objectMapper = objectMapper;
    }

    public AgentAnalysisResponseDTO investigate(
            UUID complaintId,
            String userMessage
    ) {

        ComplaintContextResponseDTO context =
                agentContextService
                        .getComplaintContext(
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

        return agentAnalysisValidator.validate(
                analysis,
                context
        );
    }
}