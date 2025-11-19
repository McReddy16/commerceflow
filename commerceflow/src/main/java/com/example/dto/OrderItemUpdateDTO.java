package com.example.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemUpdateDTO {
    @Min(value = 1, message = "quantity must be >= 1")
    private Integer quantity;
}
