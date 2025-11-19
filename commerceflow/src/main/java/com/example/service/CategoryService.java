package com.example.service;

import com.example.dto.CategoryCreateDTO;
import com.example.dto.CategoryDTO;
import com.example.dto.CategoryUpdateDTO;
import com.example.entity.Category;
import com.example.exception.BadRequestException;
import com.example.exception.ConflictException;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repo;

    /**
     * Map entity -> DTO
     */
    private CategoryDTO toDTO(Category c) {
        return new CategoryDTO(
                c.getId(),
                c.getName(),
                c.getDescription(),
                c.getCreatedAt()             // maps to DTO createdAt field
        );
    }

    /**
     * GET all with optional pagination + name filter
     */
    public Page<CategoryDTO> getAllPaged(String name, int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Category> pageData;

        if (name != null && !name.isBlank()) {
            pageData = repo.findByNameContainingIgnoreCase(name, pageable);
        } else {
            pageData = repo.findAll(pageable);
        }

        return pageData.map(this::toDTO);
    }

    /**
     * GET all without pagination (useful for exports / dropdowns)
     */
    public List<CategoryDTO> getAllWithoutPaging(String name, Sort sort) {
        if (name != null && !name.isBlank()) {
            // Use a paged call to apply sort and filtering then return content
            Pageable sorted = PageRequest.of(0, Integer.MAX_VALUE, sort);
            return repo.findByNameContainingIgnoreCase(name, sorted)
                       .map(this::toDTO)
                       .getContent();
        }

        return repo.findAll(sort).stream()
                   .map(this::toDTO)
                   .toList();
    }

    /**
     * GET by ID
     */
    public CategoryDTO getById(Long id) {
        Category c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return toDTO(c);
    }

    /**
     * CREATE
     */
    public CategoryDTO create(CategoryCreateDTO dto) {
        if (repo.existsByNameIgnoreCase(dto.getName())) {
            throw new BadRequestException("Category name already exists");
        }

        Category c = new Category();
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        // createdAt will be set by entity @PrePersist

        Category saved = repo.save(c);
        return toDTO(saved);
    }

    /**
     * UPDATE
     */
    public CategoryDTO update(Long id, CategoryUpdateDTO dto) {
        Category c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!c.getName().equalsIgnoreCase(dto.getName()) &&
                repo.existsByNameIgnoreCase(dto.getName())) {
            throw new BadRequestException("Category name already exists");
        }

        c.setName(dto.getName());
        c.setDescription(dto.getDescription());

        Category updated = repo.save(c);
        return toDTO(updated);
    }

    /**
     * DELETE
     *
     * Attempt delete and flush immediately so any FK constraint is raised now.
     * Convert DB constraint to a user-friendly ConflictException.
     */
    public void delete(Long id) {
        Category c = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        try {
            repo.delete(c);   // attempt delete
            repo.flush();     // force DB action now so we can catch integrity violations
        } catch (DataIntegrityViolationException ex) {
            // products reference this category -> return 409 Conflict-ish semantics
            throw new ConflictException("Category cannot be deleted because products reference it");
        }
    }
}
