package com.arthurnovaisdev.qualityops.service;

import com.arthurnovaisdev.qualityops.dto.request.InvestigationRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.InvestigationResponseDTO;
import com.arthurnovaisdev.qualityops.entity.Complaint;
import com.arthurnovaisdev.qualityops.entity.Investigation;
import com.arthurnovaisdev.qualityops.entity.User;
import com.arthurnovaisdev.qualityops.enums.InvestigationStatus;
import com.arthurnovaisdev.qualityops.exception.ResourceNotFoundException;
import com.arthurnovaisdev.qualityops.repository.ComplaintRepository;
import com.arthurnovaisdev.qualityops.repository.InvestigationRepository;
import com.arthurnovaisdev.qualityops.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InvestigationService {

    private final InvestigationRepository investigationRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public InvestigationResponseDTO create(
            InvestigationRequestDTO dto,
            String authenticatedEmail
    ) {
        if (investigationRepository.existsByComplaintId(dto.complaintId())) {
            throw new IllegalArgumentException(
                    "Esta reclamação já possui uma investigação."
            );
        }

        Complaint complaint = complaintRepository.findById(dto.complaintId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reclamação não encontrada.")
                );

        User createdBy = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário autenticado não encontrado."
                        )
                );

        Investigation investigation = Investigation.builder()
                .complaint(complaint)
                .analysis(dto.analysis())
                .rootCause(dto.rootCause())
                .status(InvestigationStatus.OPEN)
                .createdBy(createdBy)
                .build();

        return toResponseDTO(
                investigationRepository.save(investigation)
        );
    }

    @Transactional(readOnly = true)
    public List<InvestigationResponseDTO> findAll() {
        return investigationRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public InvestigationResponseDTO findById(UUID id) {
        return toResponseDTO(findEntityById(id));
    }

    @Transactional(readOnly = true)
    public InvestigationResponseDTO findByComplaint(UUID complaintId) {
        Investigation investigation =
                investigationRepository.findByComplaintId(complaintId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Investigação não encontrada."
                                )
                        );

        return toResponseDTO(investigation);
    }

    public InvestigationResponseDTO update(
            UUID id,
            InvestigationRequestDTO dto
    ) {
        Investigation investigation = findEntityById(id);

        if (!investigation.getComplaint().getId()
                .equals(dto.complaintId())) {

            throw new IllegalArgumentException(
                    "Não é permitido alterar a reclamação de uma investigação."
            );
        }

        investigation.setAnalysis(dto.analysis());
        investigation.setRootCause(dto.rootCause());

        return toResponseDTO(
                investigationRepository.save(investigation)
        );
    }

    private Investigation findEntityById(UUID id) {
        return investigationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Investigação não encontrada."
                        )
                );
    }

    private InvestigationResponseDTO toResponseDTO(
            Investigation investigation
    ) {
        return new InvestigationResponseDTO(
                investigation.getId(),

                investigation.getComplaint().getId(),
                investigation.getComplaint().getTitle(),

                investigation.getAnalysis(),
                investigation.getRootCause(),

                investigation.getStatus(),

                investigation.getCreatedBy().getId(),
                investigation.getCreatedBy().getName(),

                investigation.getCreatedAt(),
                investigation.getUpdatedAt()
        );
    }

    public InvestigationResponseDTO updateStatus(
            UUID id,
            InvestigationStatus newStatus
    ) {
        Investigation investigation = findEntityById(id);

        validateStatusTransition(
                investigation.getStatus(),
                newStatus
        );

        investigation.setStatus(newStatus);

        return toResponseDTO(
                investigationRepository.save(investigation)
        );
    }

    private void validateStatusTransition(
            InvestigationStatus currentStatus,
            InvestigationStatus newStatus
    ) {
        if (currentStatus == newStatus) {
            return;
        }

        boolean validTransition = switch (currentStatus) {
            case OPEN ->
                    newStatus == InvestigationStatus.IN_PROGRESS;

            case IN_PROGRESS ->
                    newStatus == InvestigationStatus.WAITING_INFORMATION
                            || newStatus == InvestigationStatus.COMPLETED;

            case WAITING_INFORMATION ->
                    newStatus == InvestigationStatus.IN_PROGRESS;

            case COMPLETED -> false;
        };

        if (!validTransition) {
            throw new IllegalArgumentException(
                    "Transição de status inválida: "
                            + currentStatus + " -> " + newStatus
            );
        }
    }
}