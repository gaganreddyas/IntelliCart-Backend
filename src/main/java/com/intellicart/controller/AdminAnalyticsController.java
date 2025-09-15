package com.intellicart.controller;

import com.intellicart.repository.OrderRepository;
import com.intellicart.repository.ProductRepository;
import com.intellicart.repository.UserRepository;
import com.intellicart.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAnalyticsController {

    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public AdminAnalyticsController(OrderService orderService,
                                    ProductRepository productRepository,
                                    UserRepository userRepository,
                                    OrderRepository orderRepository) {
        this.orderService = orderService;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/summary")
    public ResponseEntity<?> summary() {
        Map<String, Object> map = new HashMap<>();
        map.put("totalSales", orderService.totalSales());
        // top-selling products (based on order_items aggregated) - simplified via product entity sales by price * assumed qty in orders
        // We will compute top products by summing order items from orders in memory
        List<Map<String, Object>> topProducts = new ArrayList<>();
        productRepository.findAll().forEach(p -> {
            Map<String, Object> prod = new HashMap<>();
            prod.put("productId", p.getId());
            prod.put("name", p.getName());
            prod.put("category", p.getCategory());
            prod.put("stock", p.getStockQuantity());
            topProducts.add(prod);
        });
        map.put("productsSnapshot", topProducts);
        // Most active users (by number of orders)
        Map<String, Integer> userOrderCounts = new HashMap<>();
        userRepository.findAll().forEach(u -> {
            int count = orderRepository.findByUserId(u.getId()).size();
            userOrderCounts.put(u.getEmail(), count);
        });
        map.put("mostActiveUsers", userOrderCounts);
        return ResponseEntity.ok(map);
    }
}
