package com.example.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor        // required by Jackson for deserialization
@AllArgsConstructor     // useful for tests / manual construction
@Builder                // optional: keeps builder support
public class OrderCreateDTO {

    @NotNull(message = "customerId is required")
    private Long customerId;

    @NotNull(message = "items are required")
    @Size(min = 1, message = "Order must contain at least one item")
    private List<OrderItemCreateDTO> items;
}
