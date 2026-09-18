package com.arthurnovaisdev.qualityops.service;

import com.arthurnovaisdev.qualityops.dto.request.ProductRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.ProductResponseDTO;
import com.arthurnovaisdev.qualityops.entity.Product;
import com.arthurnovaisdev.qualityops.exception.ResourceNotFoundException;
import com.arthurnovaisdev.qualityops.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponseDTO create(ProductRequestDTO dto) {
        Product product = Product.builder()
                .name(dto.name())
                .code(dto.code())
                .description(dto.description())
                .build();

        return toResponseDTO(productRepository.save(product));
    }

    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ProductResponseDTO findById(UUID id) {
        return toResponseDTO(findEntityById(id));
    }

    public ProductResponseDTO update(UUID id, ProductRequestDTO dto) {
        Product product = findEntityById(id);

        product.setName(dto.name());
        product.setCode(dto.code());
        product.setDescription(dto.description());

        return toResponseDTO(productRepository.save(product));
    }

    public void delete(UUID id) {
        Product product = findEntityById(id);
        productRepository.delete(product);
    }

    private Product findEntityById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Produto não encontrado.")
                );
    }

    private ProductResponseDTO toResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getCode(),
                product.getDescription()
        );
    }
}