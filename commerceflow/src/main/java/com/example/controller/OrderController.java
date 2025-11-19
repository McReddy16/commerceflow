package com.example.controller;

import com.example.dto.OrderCreateDTO;
import com.example.dto.OrderDTO;
import com.example.dto.OrderItemDTO;
import com.example.entity.Order;
import com.example.entity.OrderItem;
import com.example.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService svc;

    public OrderController(OrderService svc) {
        this.svc = svc;
    }

    /**
     * Create order and return a safe DTO (no entity graph / no recursion).
     * Marked @Transactional so lazy associations created/loaded inside service remain accessible while mapping.
     */
    @PostMapping
    @Transactional
    public ResponseEntity<OrderDTO> create(@Valid @RequestBody OrderCreateDTO dto) {
        Order order = svc.createOrder(dto);               // persisted inside service transaction
        OrderDTO response = mapToDto(order);
        return ResponseEntity.ok(response);
    }

    /**
     * Get order by id and return DTO. @Transactional keeps session open during mapping so lazy fields can be accessed.
     */
    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<OrderDTO> get(@PathVariable Long id) {
        Order order = svc.getOrder(id);
        OrderDTO response = mapToDto(order);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        svc.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    /* -------------------------
       Inline mapping helpers
       ------------------------- */

    private OrderDTO mapToDto(Order order) {
        if (order == null) return null;

        List<OrderItemDTO> items = order.getItems() == null
                ? List.of()
                : order.getItems().stream().map(this::mapItemToDto).collect(Collectors.toList());

        return OrderDTO.builder()
                .id(order.getId())
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .customerFirstName(order.getCustomer() != null ? order.getCustomer().getFirstName() : null)
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .total(order.getTotal())
                .items(items)
                .build();
    }

    private OrderItemDTO mapItemToDto(OrderItem item) {
        if (item == null) return null;
        return new OrderItemDTO(
                item.getId(),
                item.getOrder() != null ? item.getOrder().getId() : null,
                item.getProduct() != null ? item.getProduct().getId() : null,
                item.getUnitPrice(),
                item.getQuantity(),
                item.getLineTotal()
        );
    }
}
