package com.arthurnovaisdev.qualityops.service;

import com.arthurnovaisdev.qualityops.dto.request.InvestigationSuggestionRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.InvestigationSuggestionResponseDTO;
import com.arthurnovaisdev.qualityops.entity.Complaint;
import com.arthurnovaisdev.qualityops.entity.InvestigationSuggestion;
import com.arthurnovaisdev.qualityops.enums.SuggestionStatus;
import com.arthurnovaisdev.qualityops.exception.ResourceNotFoundException;
import com.arthurnovaisdev.qualityops.repository.ComplaintRepository;
import com.arthurnovaisdev.qualityops.repository.InvestigationSuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InvestigationSuggestionService {

    private final InvestigationSuggestionRepository suggestionRepository;
    private final ComplaintRepository complaintRepository;

    public InvestigationSuggestionResponseDTO create(
            InvestigationSuggestionRequestDTO dto
    ) {
        Complaint complaint = complaintRepository.findById(dto.complaintId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Reclamação não encontrada."
                        )
                );

        InvestigationSuggestion suggestion =
                InvestigationSuggestion.builder()
                        .complaint(complaint)
                        .suggestion(dto.suggestion())
                        .status(SuggestionStatus.PENDING)
                        .build();

        return toResponseDTO(
                suggestionRepository.save(suggestion)
        );
    }

    @Transactional(readOnly = true)
    public List<InvestigationSuggestionResponseDTO> findByComplaint(
            UUID complaintId
    ) {
        if (!complaintRepository.existsById(complaintId)) {
            throw new ResourceNotFoundException(
                    "Reclamação não encontrada."
            );
        }

        return suggestionRepository
                .findByComplaintIdOrderByCreatedAtDesc(complaintId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public InvestigationSuggestionResponseDTO updateStatus(
            UUID id,
            SuggestionStatus status
    ) {
        InvestigationSuggestion suggestion =
                suggestionRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Sugestão não encontrada."
                                )
                        );

        if (suggestion.getStatus() != SuggestionStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Esta sugestão já foi revisada."
            );
        }

        if (status == SuggestionStatus.PENDING) {
            throw new IllegalArgumentException(
                    "A sugestão deve ser aceita ou rejeitada."
            );
        }

        suggestion.setStatus(status);

        return toResponseDTO(
                suggestionRepository.save(suggestion)
        );
    }

    private InvestigationSuggestionResponseDTO toResponseDTO(
            InvestigationSuggestion suggestion
    ) {
        return new InvestigationSuggestionResponseDTO(
                suggestion.getId(),
                suggestion.getComplaint().getId(),
                suggestion.getComplaint().getTitle(),
                suggestion.getSuggestion(),
                suggestion.getStatus(),
                suggestion.getCreatedAt()
        );
    }
}