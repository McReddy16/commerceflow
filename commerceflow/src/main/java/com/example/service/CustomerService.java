package com.example.service;

import com.example.dto.CustomerCreateDTO;
import com.example.dto.CustomerDTO;
import com.example.dto.CustomerUpdateDTO;
import com.example.entity.Customer;
import com.example.exception.BadRequestException;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repo;

    // Convert Entity → DTO
    private CustomerDTO toDTO(Customer c) {
        return new CustomerDTO(
                c.getId(),
                c.getFirstName(),
                c.getLastName(),
                c.getEmail(),
                c.getPhone(),
                c.getCreatedAt()
        );
    }

    public Page<CustomerDTO> getAllPaged(String name, int page, int size, Sort sort) {

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Customer> pageData = repo.findAll(pageable);

        // no filter → return normally
        if (name == null || name.isBlank()) {
            return pageData.map(this::toDTO);
        }

        // apply manual filter AFTER converting to list
        List<CustomerDTO> filtered = pageData.stream()
                .map(this::toDTO)
                .filter(dto ->
                        dto.getFirstName().toLowerCase().contains(name.toLowerCase()) ||
                        (dto.getLastName() != null &&
                         dto.getLastName().toLowerCase().contains(name.toLowerCase()))
                )
                .toList();

        return new PageImpl<>(filtered, pageable, filtered.size());
    }


    // GET all without pagination
    public List<CustomerDTO> getAllWithoutPaging(String name, Sort sort) {

        List<Customer> list = repo.findAll(sort);

        if (name != null && !name.isBlank()) {
            return list.stream()
                    .filter(c ->
                            c.getFirstName().toLowerCase().contains(name.toLowerCase())
                                    || (c.getLastName() != null &&
                                    c.getLastName().toLowerCase().contains(name.toLowerCase()))
                    )
                    .map(this::toDTO)
                    .toList();
        }

        return list.stream().map(this::toDTO).toList();
    }

    // GET by ID
    public CustomerDTO getById(Long id) {
        Customer c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return toDTO(c);
    }

    // CREATE
    public CustomerDTO create(CustomerCreateDTO dto) {

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (repo.existsByEmailIgnoreCase(dto.getEmail())) {
                throw new BadRequestException("Email already exists");
            }
        }

        Customer c = new Customer();
        c.setFirstName(dto.getFirstName());
        c.setLastName(dto.getLastName());
        c.setPhone(dto.getPhone());

        c.setEmail(dto.getEmail() != null && !dto.getEmail().isBlank()
                ? dto.getEmail().toLowerCase()
                : null
        );

        return toDTO(repo.save(c));
    }

    // UPDATE
    public CustomerDTO update(Long id, CustomerUpdateDTO dto) {

        Customer c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        String newEmail = dto.getEmail();

        if (newEmail != null && !newEmail.isBlank()) {
            repo.findByEmailIgnoreCase(newEmail).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new BadRequestException("Email already exists");
                }
            });

            c.setEmail(newEmail.toLowerCase());
        } else {
            c.setEmail(null);
        }

        c.setFirstName(dto.getFirstName());
        c.setLastName(dto.getLastName());
        c.setPhone(dto.getPhone());

        return toDTO(repo.save(c));
    }

    // DELETE
    public void delete(Long id) {
        Customer c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        repo.delete(c);
    }
}
