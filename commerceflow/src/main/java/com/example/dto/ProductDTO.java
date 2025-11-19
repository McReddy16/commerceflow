package com.example.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String sku;
    private String name;
    private Long categoryId;
    private String categoryName;
    private BigDecimal price;
    private Integer quantity;
    private Instant createdAt;
}
