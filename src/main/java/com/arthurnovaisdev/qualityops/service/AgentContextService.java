package com.arthurnovaisdev.qualityops.service;

import com.arthurnovaisdev.qualityops.dto.response.ComplaintContextResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.ComplaintResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.EvidenceResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.InvestigationResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.LotResponseDTO;
import com.arthurnovaisdev.qualityops.dto.response.agent.AgentCorrectiveActionContextDTO;
import com.arthurnovaisdev.qualityops.dto.response.agent.AgentInvestigationContextDTO;
import com.arthurnovaisdev.qualityops.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
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
                evidenceService.findByComplaint(complaintId);

        AgentInvestigationContextDTO investigation = null;

        List<AgentCorrectiveActionContextDTO> correctiveActions =
                Collections.emptyList();

        try {

            InvestigationResponseDTO investigationResponse =
                    investigationService.findByComplaint(
                            complaintId
                    );

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

        } catch (ResourceNotFoundException ignored) {
        }

        List<ComplaintResponseDTO> similarComplaints =
                complaintService.searchSimilarByComplaintId(
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