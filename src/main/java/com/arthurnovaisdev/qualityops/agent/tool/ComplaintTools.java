package com.arthurnovaisdev.qualityops.agent.tool;

import com.arthurnovaisdev.qualityops.dto.response.ComplaintResponseDTO;
import com.arthurnovaisdev.qualityops.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ComplaintTools {

    private final ComplaintService complaintService;

    @Tool(
            description = """
                    Busca os detalhes completos de uma reclamação pelo seu UUID.
                    Use esta ferramenta quando precisar analisar uma reclamação específica.
                    """
    )
    public ComplaintResponseDTO getComplaintDetails(UUID complaintId) {

        log.info(
                "AI TOOL -> getComplaintDetails | complaintId={}",
                complaintId
        );

        return complaintService.findById(complaintId);
    }
}