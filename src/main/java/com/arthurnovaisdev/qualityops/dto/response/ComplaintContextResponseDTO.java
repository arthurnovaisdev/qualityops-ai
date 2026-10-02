package com.arthurnovaisdev.qualityops.dto.response;

import com.arthurnovaisdev.qualityops.dto.response.agent.AgentCorrectiveActionContextDTO;
import com.arthurnovaisdev.qualityops.dto.response.agent.AgentInvestigationContextDTO;

import java.util.List;

public record ComplaintContextResponseDTO(
        ComplaintResponseDTO complaint,
        LotResponseDTO lot,
        List<EvidenceResponseDTO> evidences,
        AgentInvestigationContextDTO investigation,
        List<AgentCorrectiveActionContextDTO> correctiveActions,
        List<ComplaintResponseDTO> similarComplaints
) {
}