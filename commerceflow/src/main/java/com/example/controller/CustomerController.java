package com.example.controller;

import com.example.dto.CustomerCreateDTO;
import com.example.dto.CustomerDTO;
import com.example.dto.CustomerUpdateDTO;
import com.example.service.CustomerService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService svc;

    // GET all customers (with optional pagination + name filter)
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        if (page != null && size != null) {
            return ResponseEntity.ok(svc.getAllPaged(name, page, size, sort));
        }

        List<CustomerDTO> list = svc.getAllWithoutPaging(name, sort);
        return ResponseEntity.ok(list);
    }

    // GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(svc.getById(id));
    }

    // CREATE
    @PostMapping
    public ResponseEntity<CustomerDTO> create(@Valid @RequestBody CustomerCreateDTO dto) {
        CustomerDTO created = svc.create(dto);
        URI location = URI.create("/api/customers/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerUpdateDTO dto
    ) {
        return ResponseEntity.ok(svc.update(id, dto));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }
}
