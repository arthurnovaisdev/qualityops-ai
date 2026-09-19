package com.arthurnovaisdev.qualityops.service;

import com.arthurnovaisdev.qualityops.dto.request.LotRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.LotResponseDTO;
import com.arthurnovaisdev.qualityops.entity.Lot;
import com.arthurnovaisdev.qualityops.entity.Product;
import com.arthurnovaisdev.qualityops.exception.ResourceNotFoundException;
import com.arthurnovaisdev.qualityops.repository.LotRepository;
import com.arthurnovaisdev.qualityops.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LotService {

    private final LotRepository lotRepository;
    private final ProductRepository productRepository;

    public LotResponseDTO create(LotRequestDTO dto) {
        if (lotRepository.existsByCode(dto.code())) {
            throw new IllegalArgumentException("Já existe um lote com esse código.");
        }

        validateDates(dto);

        Product product = findProductById(dto.productId());

        Lot lot = Lot.builder()
                .code(dto.code())
                .product(product)
                .manufacturingDate(dto.manufacturingDate())
                .expirationDate(dto.expirationDate())
                .build();

        return toResponseDTO(lotRepository.save(lot));
    }

    @Transactional(readOnly = true)
    public List<LotResponseDTO> findAll() {
        return lotRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public LotResponseDTO findById(UUID id) {
        return toResponseDTO(findEntityById(id));
    }

    public LotResponseDTO update(UUID id, LotRequestDTO dto) {
        Lot lot = findEntityById(id);

        lotRepository.findByCode(dto.code())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Já existe um lote com esse código."
                    );
                });

        validateDates(dto);

        Product product = findProductById(dto.productId());

        lot.setCode(dto.code());
        lot.setProduct(product);
        lot.setManufacturingDate(dto.manufacturingDate());
        lot.setExpirationDate(dto.expirationDate());

        return toResponseDTO(lotRepository.save(lot));
    }

    public void delete(UUID id) {
        Lot lot = findEntityById(id);
        lotRepository.delete(lot);
    }

    private Lot findEntityById(UUID id) {
        return lotRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Lote não encontrado.")
                );
    }

    private Product findProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Produto não encontrado.")
                );
    }

    private void validateDates(LotRequestDTO dto) {
        if (dto.manufacturingDate() != null
                && dto.expirationDate() != null
                && dto.expirationDate().isBefore(dto.manufacturingDate())) {

            throw new IllegalArgumentException(
                    "A data de validade não pode ser anterior à data de fabricação."
            );
        }
    }

    private LotResponseDTO toResponseDTO(Lot lot) {
        return new LotResponseDTO(
                lot.getId(),
                lot.getCode(),
                lot.getProduct().getId(),
                lot.getProduct().getName(),
                lot.getManufacturingDate(),
                lot.getExpirationDate()
        );
    }
}