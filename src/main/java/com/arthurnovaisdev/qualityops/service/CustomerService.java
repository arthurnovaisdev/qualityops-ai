package com.arthurnovaisdev.qualityops.service;


import com.arthurnovaisdev.qualityops.dto.request.CustomerRequestDTO;
import com.arthurnovaisdev.qualityops.dto.response.CustomerResponseDTO;
import com.arthurnovaisdev.qualityops.entity.Customer;
import com.arthurnovaisdev.qualityops.exception.ResourceNotFoundException;
import com.arthurnovaisdev.qualityops.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerResponseDTO create(CustomerRequestDTO dto) {

        Customer customer = Customer.builder()
                .name(dto.name())
                .document(dto.document())
                .contact(dto.contact())
                .build();

        return toResponseDTO(customerRepository.save(customer));
    }

    public List<CustomerResponseDTO> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public CustomerResponseDTO findById(UUID id) {
        return toResponseDTO(findEntityById(id));
    }

    public CustomerResponseDTO update(UUID id, CustomerRequestDTO dto) {

        Customer customer = findEntityById(id);

        customer.setName(dto.name());
        customer.setDocument(dto.document());
        customer.setContact(dto.contact());

        return toResponseDTO(customerRepository.save(customer));
    }

    public void delete(UUID id) {
        Customer customer = findEntityById(id);
        customerRepository.delete(customer);
    }

    private Customer findEntityById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cliente não encontrado.")
                );
    }

    private CustomerResponseDTO toResponseDTO(Customer customer) {
        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getDocument(),
                customer.getContact()
        );
    }
}
