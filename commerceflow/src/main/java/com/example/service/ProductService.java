package com.example.service;

import com.example.dto.*;
import com.example.entity.Product;
import com.example.exception.BadRequestException;
import com.example.exception.ConflictException;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.CategoryRepository;
import com.example.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductDTO create(ProductCreateDTO dto) {
        validatePriceAndQuantity(dto.getPrice(), dto.getQuantity());

        if (productRepository.existsBySku(dto.getSku())) {
            throw new BadRequestException("SKU already exists: " + dto.getSku());
        }

        var category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + dto.getCategoryId()));

        Product product = Product.builder()
                .sku(dto.getSku().trim())
                .name(dto.getName().trim())
                .category(category)
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .build();

        Product saved = productRepository.save(product);
        return toDTO(saved);
    }

    public ProductDTO update(Long id, ProductUpdateDTO dto) {
        validatePriceAndQuantity(dto.getPrice(), dto.getQuantity());

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        var category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + dto.getCategoryId()));

        existing.setName(dto.getName().trim());
        existing.setCategory(category);
        existing.setPrice(dto.getPrice());
        existing.setQuantity(dto.getQuantity());

        Product saved = productRepository.save(existing);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public ProductDTO getById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return toDTO(p);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAll() {
        return productRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

        public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        try {
            productRepository.delete(product);
            productRepository.flush(); // force SQL to run now so we can catch FK violations here
        } catch (DataIntegrityViolationException ex) {
            // keep message user-friendly and consistent with CategoryService
            throw new ConflictException("Product cannot be deleted because orders reference it");
        }
    }

    private void validatePriceAndQuantity(BigDecimal price, Integer quantity) {
        if (price == null) {
            throw new BadRequestException("price is required");
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("price must be non-negative");
        }
        if (quantity == null) {
            throw new BadRequestException("quantity is required");
        }
        if (quantity < 0) {
            throw new BadRequestException("quantity must be non-negative");
        }
    }

    private ProductDTO toDTO(Product p) {
        return ProductDTO.builder()
                .id(p.getId())
                .sku(p.getSku())
                .name(p.getName())
                .categoryId(p.getCategory().getId())
                .categoryName(p.getCategory().getName())
                .price(p.getPrice())
                .quantity(p.getQuantity())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
