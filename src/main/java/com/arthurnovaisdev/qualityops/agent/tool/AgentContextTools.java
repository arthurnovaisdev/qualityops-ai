package com.arthurnovaisdev.qualityops.agent.tool;

import com.arthurnovaisdev.qualityops.dto.response.ComplaintContextResponseDTO;
import com.arthurnovaisdev.qualityops.service.AgentContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgentContextTools {

    private final AgentContextService agentContextService;

    @Tool(
            description = """
                    Busca todo o contexto necessário para analisar uma reclamação:
                    dados da reclamação, lote, evidências, investigação existente
                    e ações corretivas.

                    Use esta ferramenta antes de analisar uma reclamação.
                    O único argumento necessário é o UUID da reclamação.
                    """
    )
    public ComplaintContextResponseDTO getComplaintContext(
            UUID complaintId
    ) {
        log.info(
                "AI TOOL -> getComplaintContext | complaintId={}",
                complaintId
        );

        return agentContextService.getComplaintContext(
                complaintId
        );
    }
}