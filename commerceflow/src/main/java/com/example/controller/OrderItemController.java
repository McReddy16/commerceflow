package com.example.controller;

import com.example.dto.OrderItemCreateDTO;
import com.example.dto.OrderItemDTO;
import com.example.dto.OrderItemUpdateDTO;
import com.example.service.OrderItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderItemController {

    private final OrderItemService svc;

    public OrderItemController(OrderItemService svc) {
        this.svc = svc;
    }

    // Add item to order (nested)
    @PostMapping("/orders/{orderId}/items")
    public ResponseEntity<OrderItemDTO> addToOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderItemCreateDTO dto
    ) {
        OrderItemDTO saved = svc.addItem(orderId, dto);
        return ResponseEntity.ok(saved); // 200 OK; change to created(...) if you prefer 201
    }

    // List items for an order (nested)
    @GetMapping("/orders/{orderId}/items")
    public ResponseEntity<List<OrderItemDTO>> listByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(svc.getItemsByOrder(orderId));
    }

    // Get single item (top-level)
    @GetMapping("/order-items/{id}")
    public ResponseEntity<OrderItemDTO> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(svc.getItem(id));
    }

    // Update item quantity (top-level)
    @PutMapping("/order-items/{id}")
    public ResponseEntity<OrderItemDTO> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody OrderItemUpdateDTO dto
    ) {
        return ResponseEntity.ok(svc.updateItem(id, dto));
    }

    // Delete item
    @DeleteMapping("/order-items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        svc.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
