package com.example.repository;

import com.example.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // email checks (your existing methods)
    boolean existsByEmailIgnoreCase(String email);
    Optional<Customer> findByEmailIgnoreCase(String email);

    // phone number checks
    boolean existsByPhone(String phone);
    Optional<Customer> findByPhone(String phone);

    // firstName + lastName uniqueness check
    boolean existsByFirstNameAndLastName(String firstName, String lastName);
}
