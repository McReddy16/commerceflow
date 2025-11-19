package com.example.service;

import com.example.dto.OrderCreateDTO;
import com.example.dto.OrderItemCreateDTO;
import com.example.entity.Customer;
import com.example.entity.Order;
import com.example.entity.OrderItem;
import com.example.entity.Product;
import com.example.exception.BadRequestException;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.OrderRepository;
import com.example.repository.CustomerRepository;
import com.example.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;
    private final ProductRepository productRepo;

    public OrderService(OrderRepository orderRepo,
                        CustomerRepository customerRepo,
                        ProductRepository productRepo) {
        this.orderRepo = orderRepo;
        this.customerRepo = customerRepo;
        this.productRepo = productRepo;
    }

    /**
     * Create an order with items. All monetary values use BigDecimal.
     */
    @Transactional
    public Order createOrder(OrderCreateDTO dto) {

        Customer customer = customerRepo.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Order order = new Order();
        order.setCustomer(customer);

        // Use BigDecimal for money calculations
        BigDecimal orderTotal = BigDecimal.ZERO;

        for (OrderItemCreateDTO i : dto.getItems()) {

            if (i.getQuantity() == null || i.getQuantity() <= 0) {
                throw new BadRequestException("Quantity must be greater than 0");
            }

            Product product = productRepo.findById(i.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            // create and populate item
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(i.getQuantity());

            // product.getPrice() is BigDecimal (entity must use BigDecimal)
            item.setUnitPrice(product.getPrice());

            // lineTotal = price * quantity
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(i.getQuantity()));
            item.setLineTotal(lineTotal);

            // attach
            order.getItems().add(item);

            // accumulate
            orderTotal = orderTotal.add(lineTotal);
        }

        order.setTotal(orderTotal);

        // cascade should persist items; save order
        return orderRepo.save(order);
    }

    public Order getOrder(Long id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        orderRepo.delete(order);
    }
}
