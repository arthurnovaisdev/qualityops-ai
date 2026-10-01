package com.arthurnovaisdev.qualityops.service;

import com.arthurnovaisdev.qualityops.dto.request.EvidenceRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.EvidenceResponseDTO;
import com.arthurnovaisdev.qualityops.entity.Complaint;
import com.arthurnovaisdev.qualityops.entity.Evidence;
import com.arthurnovaisdev.qualityops.entity.User;
import com.arthurnovaisdev.qualityops.exception.ResourceNotFoundException;
import com.arthurnovaisdev.qualityops.repository.ComplaintRepository;
import com.arthurnovaisdev.qualityops.repository.EvidenceRepository;
import com.arthurnovaisdev.qualityops.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public EvidenceResponseDTO create(
            EvidenceRequestDTO dto,
            String authenticatedEmail
    ) {
        Complaint complaint = complaintRepository.findById(dto.complaintId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reclamação não encontrada.")
                );

        User createdBy = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuário autenticado não encontrado.")
                );

        Evidence evidence = Evidence.builder()
                .complaint(complaint)
                .title(dto.title())
                .description(dto.description())
                .fileUrl(dto.fileUrl())
                .createdBy(createdBy)
                .build();

        return toResponseDTO(
                evidenceRepository.save(evidence)
        );
    }

    @Transactional(readOnly = true)
    public List<EvidenceResponseDTO> findByComplaint(UUID complaintId) {

        if (!complaintRepository.existsById(complaintId)) {
            throw new ResourceNotFoundException(
                    "Reclamação não encontrada."
            );
        }

        return evidenceRepository
                .findByComplaintId(complaintId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EvidenceResponseDTO findById(UUID id) {
        return toResponseDTO(
                findEntityById(id)
        );
    }

    public void delete(UUID id) {
        Evidence evidence = findEntityById(id);
        evidenceRepository.delete(evidence);
    }

    private Evidence findEntityById(UUID id) {
        return evidenceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Evidência não encontrada.")
                );
    }

    private EvidenceResponseDTO toResponseDTO(Evidence evidence) {
        return new EvidenceResponseDTO(
                evidence.getId(),
                evidence.getComplaint().getId(),
                evidence.getComplaint().getTitle(),
                evidence.getTitle(),
                evidence.getDescription(),
                evidence.getFileUrl(),
                evidence.getCreatedBy().getId(),
                evidence.getCreatedBy().getName(),
                evidence.getCreatedAt()
        );
    }
}