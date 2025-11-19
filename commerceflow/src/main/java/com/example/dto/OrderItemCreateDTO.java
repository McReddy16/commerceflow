package com.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderItemCreateDTO {

    @NotNull
    private Long productId;

    @Min(1)
    private Integer quantity;
}
