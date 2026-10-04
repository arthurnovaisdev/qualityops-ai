package com.arthurnovaisdev.qualityops.service;

import com.arthurnovaisdev.qualityops.dto.request.ComplaintRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.ComplaintResponseDTO;
import com.arthurnovaisdev.qualityops.entity.*;
import com.arthurnovaisdev.qualityops.event.ComplaintChangedEvent;
import com.arthurnovaisdev.qualityops.enums.ComplaintStatus;
import com.arthurnovaisdev.qualityops.exception.ResourceNotFoundException;
import com.arthurnovaisdev.qualityops.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final LotRepository lotRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ComplaintResponseDTO create(
            ComplaintRequestDTO dto,
            String authenticatedEmail
    ) {
        Customer customer = findCustomer(dto.customerId());
        Product product = findProduct(dto.productId());
        Lot lot = findAndValidateLot(dto.lotId(), product);

        User createdBy = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuário autenticado não encontrado."
                        )
                );

        Complaint complaint = Complaint.builder()
                .title(dto.title())
                .description(dto.description())
                .customer(customer)
                .product(product)
                .lot(lot)
                .createdBy(createdBy)
                .status(ComplaintStatus.OPEN)
                .build();

        Complaint saved =
                complaintRepository.save(complaint);

        eventPublisher.publishEvent(
                new ComplaintChangedEvent(saved.getId())
        );

        return toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponseDTO> findAll() {
        return complaintRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ComplaintResponseDTO findById(UUID id) {
        return toResponseDTO(findEntityById(id));
    }

    public ComplaintResponseDTO update(
            UUID id,
            ComplaintRequestDTO dto
    ) {
        Complaint complaint = findEntityById(id);

        Customer customer = findCustomer(dto.customerId());
        Product product = findProduct(dto.productId());
        Lot lot = findAndValidateLot(dto.lotId(), product);

        complaint.setTitle(dto.title());
        complaint.setDescription(dto.description());
        complaint.setCustomer(customer);
        complaint.setProduct(product);
        complaint.setLot(lot);

        return toResponseDTO(complaintRepository.save(complaint));
    }

    private Complaint findEntityById(UUID id) {
        return complaintRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Reclamação não encontrada.")
                );
    }

    private Customer findCustomer(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cliente não encontrado.")
                );
    }

    private Product findProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Produto não encontrado.")
                );
    }

    private Lot findAndValidateLot(UUID lotId, Product product) {
        if (lotId == null) {
            return null;
        }

        Lot lot = lotRepository.findById(lotId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Lote não encontrado.")
                );

        if (!lot.getProduct().getId().equals(product.getId())) {
            throw new IllegalArgumentException(
                    "O lote informado não pertence ao produto selecionado."
            );
        }

        return lot;
    }

    private ComplaintResponseDTO toResponseDTO(Complaint complaint) {
        Lot lot = complaint.getLot();

        return new ComplaintResponseDTO(
                complaint.getId(),
                complaint.getTitle(),
                complaint.getDescription(),

                complaint.getCustomer().getId(),
                complaint.getCustomer().getName(),

                complaint.getProduct().getId(),
                complaint.getProduct().getName(),

                lot != null ? lot.getId() : null,
                lot != null ? lot.getCode() : null,

                complaint.getCreatedBy().getId(),
                complaint.getCreatedBy().getName(),

                complaint.getStatus(),
                complaint.getCreatedAt(),
                complaint.getUpdatedAt()
        );
    }

    public ComplaintResponseDTO updateStatus(
            UUID id,
            ComplaintStatus newStatus
    ) {
        Complaint complaint = findEntityById(id);

        validateStatusTransition(
                complaint.getStatus(),
                newStatus
        );

        complaint.setStatus(newStatus);

        return toResponseDTO(
                complaintRepository.save(complaint)
        );
    }

    private void validateStatusTransition(
            ComplaintStatus currentStatus,
            ComplaintStatus newStatus
    ) {
        if (currentStatus == newStatus) {
            return;
        }

        boolean validTransition = switch (currentStatus) {
            case OPEN ->
                    newStatus == ComplaintStatus.UNDER_ANALYSIS;

            case UNDER_ANALYSIS ->
                    newStatus == ComplaintStatus.INVESTIGATING;

            case INVESTIGATING ->
                    newStatus == ComplaintStatus.RESOLVED;

            case RESOLVED ->
                    newStatus == ComplaintStatus.CLOSED;

            case CLOSED -> false;
        };

        if (!validTransition) {
            throw new IllegalArgumentException(
                    "Transição de status inválida: "
                            + currentStatus + " -> " + newStatus
            );
        }
    }
}