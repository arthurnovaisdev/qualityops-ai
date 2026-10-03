package com.arthurnovaisdev.qualityops.service;

import com.arthurnovaisdev.qualityops.dto.response.ComplaintContextResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.ComplaintResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.EvidenceResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.InvestigationResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.LotResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.agent.AgentCorrectiveActionContextDTO;
import com.arthurnovaisdev.qualityops.dto.response.agent.AgentInvestigationContextDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentContextService {

    private final ComplaintService complaintService;
    private final LotService lotService;
    private final EvidenceService evidenceService;
    private final InvestigationService investigationService;
    private final CorrectiveActionService correctiveActionService;
    private final ComplaintVectorService complaintVectorService;

    public ComplaintContextResponseDTO getComplaintContext(
            UUID complaintId
    ) {

        ComplaintResponseDTO complaint =
                complaintService.findById(complaintId);

        LotResponseDTO lot = null;

        if (complaint.lotCode() != null) {
            lot = lotService.findByCode(
                    complaint.lotCode()
            );
        }

        List<EvidenceResponseDTO> evidences =
                evidenceService.findByComplaint(
                        complaintId
                );

        AgentInvestigationContextDTO investigation = null;

        List<AgentCorrectiveActionContextDTO> correctiveActions =
                List.of();

        Optional<InvestigationResponseDTO> investigationOptional =
                investigationService.findOptionalByComplaint(
                        complaintId
                );

        if (investigationOptional.isPresent()) {

            InvestigationResponseDTO investigationResponse =
                    investigationOptional.get();

            investigation =
                    new AgentInvestigationContextDTO(
                            investigationResponse.analysis(),
                            investigationResponse.rootCause(),
                            investigationResponse.status().toString(),
                            investigationResponse.createdByName()
                    );

            correctiveActions =
                    correctiveActionService
                            .findByInvestigation(
                                    investigationResponse.id()
                            )
                            .stream()
                            .map(action ->
                                    new AgentCorrectiveActionContextDTO(
                                            action.description(),
                                            action.responsibleName(),
                                            action.status().toString(),
                                            action.completedAt() != null,
                                            action.dueDate(),
                                            action.completedAt(),
                                            false
                                    )
                            )
                            .toList();
        }

        List<ComplaintResponseDTO> similarComplaints =
                complaintVectorService.searchSimilarComplaints(
                        complaintId
                );

        return new ComplaintContextResponseDTO(
                complaint,
                lot,
                evidences,
                investigation,
                correctiveActions,
                similarComplaints
        );
    }
}