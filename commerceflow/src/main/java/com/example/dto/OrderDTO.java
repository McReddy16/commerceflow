package com.example.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private Long customerId;
    private String customerFirstName;
    private Instant orderDate;
    private String status;
    private BigDecimal total;
    private List<OrderItemDTO> items;
}
