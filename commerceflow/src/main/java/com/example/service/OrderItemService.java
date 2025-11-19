package com.example.service;

import com.example.dto.OrderItemCreateDTO;
import com.example.dto.OrderItemDTO;
import com.example.dto.OrderItemUpdateDTO;
import com.example.entity.Order;
import com.example.entity.OrderItem;
import com.example.entity.Product;
import com.example.exception.BadRequestException;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.OrderItemRepository;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderItemService {

    private final OrderItemRepository itemRepo;
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;

    public OrderItemService(OrderItemRepository itemRepo,
                            OrderRepository orderRepo,
                            ProductRepository productRepo) {
        this.itemRepo = itemRepo;
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    // Convert entity -> DTO
    private OrderItemDTO toDto(OrderItem it) {
        return new OrderItemDTO(
                it.getId(),
                it.getOrder() != null ? it.getOrder().getId() : null,
                it.getProduct() != null ? it.getProduct().getId() : null,
                it.getUnitPrice(),
                it.getQuantity(),
                it.getLineTotal()
        );
    }

    /**
     * Add an item to an existing order.
     * Validates quantity, product and updates order.total atomically (transaction).
     */
    @Transactional
    public OrderItemDTO addItem(Long orderId, OrderItemCreateDTO dto) {
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());

        BigDecimal unitPrice = product.getPrice();
        if (unitPrice == null) unitPrice = BigDecimal.ZERO;
        item.setUnitPrice(unitPrice);

        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(dto.getQuantity()));
        item.setLineTotal(lineTotal);

        // Persist the item
        OrderItem saved = itemRepo.save(item);

        // Update order total and persist order
        BigDecimal currentTotal = order.getTotal() == null ? BigDecimal.ZERO : order.getTotal();
        order.setTotal(currentTotal.add(lineTotal));
        orderRepo.save(order);

        return toDto(saved);
    }

    public OrderItemDTO getItem(Long itemId) {
        OrderItem it = itemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));
        return toDto(it);
    }

    /**
     * List items for the given order.
     */
    public List<OrderItemDTO> getItemsByOrder(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return order.getItems()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Update quantity of an item and adjust order total.
     */
    @Transactional
    public OrderItemDTO updateItem(Long itemId, OrderItemUpdateDTO dto) {
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        OrderItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

        Order order = item.getOrder();
        if (order == null) throw new ResourceNotFoundException("Parent order not found");

        BigDecimal oldLine = item.getLineTotal() == null ? BigDecimal.ZERO : item.getLineTotal();

        // Subtract old line total
        BigDecimal orderTotal = order.getTotal() == null ? BigDecimal.ZERO : order.getTotal();
        order.setTotal(orderTotal.subtract(oldLine));

        // Update item
        item.setQuantity(dto.getQuantity());
        BigDecimal newLine = (item.getUnitPrice() == null ? BigDecimal.ZERO : item.getUnitPrice())
                .multiply(BigDecimal.valueOf(dto.getQuantity()));
        item.setLineTotal(newLine);

        OrderItem saved = itemRepo.save(item);

        // Add new line total and persist order
        order.setTotal(order.getTotal().add(newLine));
        orderRepo.save(order);

        return toDto(saved);
    }

    /**
     * Delete item and subtract its line total from order.
     */
    @Transactional
    public void deleteItem(Long itemId) {
        OrderItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

        Order order = item.getOrder();
        if (order == null) {
            // safe fallback: delete item only
            itemRepo.delete(item);
            return;
        }

        BigDecimal line = item.getLineTotal() == null ? BigDecimal.ZERO : item.getLineTotal();

        // Delete item
        itemRepo.delete(item);

        // Update order total
        BigDecimal orderTotal = order.getTotal() == null ? BigDecimal.ZERO : order.getTotal();
        order.setTotal(orderTotal.subtract(line));
        orderRepo.save(order);
    }
}
