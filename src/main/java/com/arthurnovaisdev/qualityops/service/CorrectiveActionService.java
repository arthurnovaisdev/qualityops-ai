package com.arthurnovaisdev.qualityops.service;

import com.arthurnovaisdev.qualityops.dto.request.CorrectiveActionRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.CorrectiveActionResponseDTO;
import com.arthurnovaisdev.qualityops.entity.CorrectiveAction;
import com.arthurnovaisdev.qualityops.entity.Investigation;
import com.arthurnovaisdev.qualityops.entity.User;
import com.arthurnovaisdev.qualityops.enums.CorrectiveActionStatus;
import com.arthurnovaisdev.qualityops.exception.ResourceNotFoundException;
import com.arthurnovaisdev.qualityops.repository.CorrectiveActionRepository;
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
public class CorrectiveActionService {

    private final CorrectiveActionRepository correctiveActionRepository;
    private final InvestigationRepository investigationRepository;
    private final UserRepository userRepository;

    public CorrectiveActionResponseDTO create(
            CorrectiveActionRequestDTO dto,
            String authenticatedEmail
    ) {
        Investigation investigation = findInvestigation(dto.investigationId());
        User responsible = findUser(dto.responsibleId());

        User createdBy = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário autenticado não encontrado."
                        )
                );

        CorrectiveAction action = CorrectiveAction.builder()
                .investigation(investigation)
                .description(dto.description())
                .responsible(responsible)
                .dueDate(dto.dueDate())
                .status(CorrectiveActionStatus.PENDING)
                .createdBy(createdBy)
                .build();

        return toResponseDTO(
                correctiveActionRepository.save(action)
        );
    }

    @Transactional(readOnly = true)
    public List<CorrectiveActionResponseDTO> findAll() {
        return correctiveActionRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CorrectiveActionResponseDTO findById(UUID id) {
        return toResponseDTO(
                findEntityById(id)
        );
    }

    @Transactional(readOnly = true)
    public List<CorrectiveActionResponseDTO> findByInvestigation(
            UUID investigationId
    ) {
        return correctiveActionRepository
                .findByInvestigationId(investigationId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public CorrectiveActionResponseDTO update(
            UUID id,
            CorrectiveActionRequestDTO dto
    ) {
        CorrectiveAction action = findEntityById(id);

        if (!action.getInvestigation().getId()
                .equals(dto.investigationId())) {

            throw new IllegalArgumentException(
                    "Não é permitido alterar a investigação da ação corretiva."
            );
        }

        User responsible = findUser(dto.responsibleId());

        action.setDescription(dto.description());
        action.setResponsible(responsible);
        action.setDueDate(dto.dueDate());

        return toResponseDTO(
                correctiveActionRepository.save(action)
        );
    }

    private CorrectiveAction findEntityById(UUID id) {
        return correctiveActionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ação corretiva não encontrada."
                        )
                );
    }

    private Investigation findInvestigation(UUID id) {
        return investigationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Investigação não encontrada."
                        )
                );
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário responsável não encontrado."
                        )
                );
    }

    private CorrectiveActionResponseDTO toResponseDTO(
            CorrectiveAction action
    ) {
        return new CorrectiveActionResponseDTO(
                action.getId(),
                action.getInvestigation().getId(),
                action.getDescription(),

                action.getResponsible().getId(),
                action.getResponsible().getName(),

                action.getDueDate(),
                action.getStatus(),

                action.getCreatedBy().getId(),
                action.getCreatedBy().getName(),

                action.getCreatedAt(),
                action.getUpdatedAt(),
                action.getCompletedAt()
        );
    }

    public CorrectiveActionResponseDTO updateStatus(
            UUID id,
            CorrectiveActionStatus newStatus
    ) {
        CorrectiveAction action = findEntityById(id);

        validateStatusTransition(
                action.getStatus(),
                newStatus
        );

        action.setStatus(newStatus);

        if (newStatus == CorrectiveActionStatus.COMPLETED) {
            action.setCompletedAt(java.time.LocalDateTime.now());
        } else {
            action.setCompletedAt(null);
        }

        return toResponseDTO(
                correctiveActionRepository.save(action)
        );
    }

    private void validateStatusTransition(
            CorrectiveActionStatus currentStatus,
            CorrectiveActionStatus newStatus
    ) {
        if (currentStatus == newStatus) {
            return;
        }

        boolean validTransition = switch (currentStatus) {

            case PENDING ->
                    newStatus == CorrectiveActionStatus.IN_PROGRESS
                            || newStatus == CorrectiveActionStatus.CANCELLED;

            case IN_PROGRESS ->
                    newStatus == CorrectiveActionStatus.COMPLETED
                            || newStatus == CorrectiveActionStatus.CANCELLED;

            case OVERDUE ->
                    newStatus == CorrectiveActionStatus.IN_PROGRESS
                            || newStatus == CorrectiveActionStatus.COMPLETED
                            || newStatus == CorrectiveActionStatus.CANCELLED;

            case COMPLETED, CANCELLED -> false;
        };

        if (!validTransition) {
            throw new IllegalArgumentException(
                    "Transição de status inválida: "
                            + currentStatus + " -> " + newStatus
            );
        }
    }
}