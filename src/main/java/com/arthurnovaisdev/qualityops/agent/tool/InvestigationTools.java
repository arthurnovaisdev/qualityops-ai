package com.arthurnovaisdev.qualityops.agent.tool;

import com.arthurnovaisdev.qualityops.dto.response.InvestigationResponseDTO;
import com.arthurnovaisdev.qualityops.service.InvestigationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestigationTools {

    private final InvestigationService investigationService;

    @Tool(
            description = """
                    Busca os dados da investigação relacionada a uma reclamação.
                    Use esta ferramenta para consultar análise, causa raiz confirmada
                    e status da investigação.
                    """
    )
    public InvestigationResponseDTO getInvestigationHistory(
            UUID complaintId
    ) {

        log.info(
                "AI TOOL -> getInvestigationHistory | complaintId={}",
                complaintId
        );

        return investigationService.findByComplaint(
                complaintId
        );
    }
}