package com.intellicart.controller;

import com.intellicart.model.OrderEntity;
import com.intellicart.repository.UserRepository;
import com.intellicart.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final UserRepository userRepo;

    public OrderController(OrderService orderService, UserRepository userRepo) {
        this.orderService = orderService;
        this.userRepo = userRepo;
    }

    private Long getCurrentUserId(Authentication auth) {
        String email = (String) auth.getPrincipal();
        return userRepo.findByEmail(email).orElseThrow().getId();
    }

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(Authentication auth) {
        Long userId = getCurrentUserId(auth);
        try {
            OrderEntity order = orderService.placeOrder(userId);
            return ResponseEntity.ok(order);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderEntity>> myOrders(Authentication auth) {
        Long userId = getCurrentUserId(auth);
        return ResponseEntity.ok(orderService.getOrdersForUser(userId));
    }
}
