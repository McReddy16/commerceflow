package com.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_customer_name", columnNames = {"first_name", "last_name"}),
           @UniqueConstraint(name = "uk_customer_phone", columnNames = {"phone"}),
           @UniqueConstraint(name = "uk_customer_email", columnNames = {"email"})
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "first_name",unique = true, nullable = false, updatable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false, updatable = false)
    private String lastName;


    @Column(name = "email", unique = true, updatable = false)
    private String email;

    @Column(name = "phone", nullable = false, unique = true, length = 10, updatable = false)
    private String phone;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    // When a customer is deleted, all their orders must delete automatically
    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();
}
