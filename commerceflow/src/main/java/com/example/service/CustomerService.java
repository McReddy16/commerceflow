package com.example.service;

import com.example.dto.CustomerCreateDTO;
import com.example.dto.CustomerDTO;
import com.example.dto.CustomerUpdateDTO;
import com.example.entity.Customer;
import com.example.exception.BadRequestException;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repo;

    // Convert Entity → DTO (includes address now)
    private CustomerDTO toDTO(Customer c) {
        return new CustomerDTO(
                c.getId(),
                c.getFirstName(),
                c.getLastName(),
                c.getEmail(),
                c.getPhone(),
                c.getAddress(),
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
    @Transactional
    public CustomerDTO create(CustomerCreateDTO dto) {

        // normalize and trim inputs
        String firstName = dto.getFirstName() != null ? dto.getFirstName().trim() : null;
        String lastName = dto.getLastName() != null ? dto.getLastName().trim() : null;
        String phone = dto.getPhone() != null ? dto.getPhone().trim() : null;
        String email = dto.getEmail() != null && !dto.getEmail().isBlank() ? dto.getEmail().trim().toLowerCase() : null;
        String address = dto.getAddress() != null ? dto.getAddress().trim() : null;

        // Basic null checks (DTO has validation, but double-check to be safe)
        if (firstName == null || firstName.isBlank()) {
            throw new BadRequestException("firstName is required");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new BadRequestException("lastName is required");
        }
        if (phone == null || phone.isBlank()) {
            throw new BadRequestException("phone is required");
        }
        if (address == null || address.isBlank()) {
            throw new BadRequestException("address is required");
        }

        // Uniqueness checks
        if (repo.existsByPhone(phone)) {
            throw new BadRequestException("Phone already in use");
        }
        if (repo.existsByFirstNameAndLastName(firstName, lastName)) {
            throw new BadRequestException("Customer with same first name and last name already exists");
        }
        if (email != null && !email.isBlank() && repo.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("Email already in use");
        }

        Customer c = new Customer();
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setPhone(phone);
        c.setEmail(email);
        c.setAddress(address);

        try {
            Customer saved = repo.save(c);
            return toDTO(saved);
        } catch (DataIntegrityViolationException ex) {
            // handle DB-level unique constraint race conditions
            throw new BadRequestException("Unique constraint violated: " + ex.getMostSpecificCause().getMessage());
        }
    }

    // UPDATE (only address is updatable per rules)
    @Transactional
    public CustomerDTO update(Long id, CustomerUpdateDTO dto) {

        Customer c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (dto == null) {
            throw new BadRequestException("Empty update payload");
        }

        // Only address is allowed to change
        String newAddress = dto.getAddress() != null ? dto.getAddress().trim() : null;
        if (newAddress == null || newAddress.isBlank()) {
            throw new BadRequestException("address is required");
        }

        c.setAddress(newAddress);

        try {
            Customer saved = repo.save(c);
            return toDTO(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Constraint violation during update: " + ex.getMostSpecificCause().getMessage());
        }
    }

    // DELETE
    public void delete(Long id) {
        Customer c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        repo.delete(c);
    }
}
