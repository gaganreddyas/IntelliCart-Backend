package com.intellicart.controller;

import com.intellicart.model.CartItem;
import com.intellicart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    private final com.intellicart.repository.UserRepository userRepo;

    public CartController(CartService cartService, com.intellicart.repository.UserRepository userRepo) {
        this.cartService = cartService;
        this.userRepo = userRepo;
    }

    private Long getCurrentUserId(Authentication auth) {
        String email = (String) auth.getPrincipal();
        return userRepo.findByEmail(email).orElseThrow().getId();
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(Authentication auth) {
        Long userId = getCurrentUserId(auth);
        return ResponseEntity.ok(cartService.getCartForUser(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(Authentication auth, @RequestBody AddReq req) {
        Long userId = getCurrentUserId(auth);
        try {
            var ci = cartService.addToCart(userId, req.getProductId(), req.getQuantity());
            return ResponseEntity.ok(ci);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeFromCart(Authentication auth, @RequestBody AddReq req) {
        Long userId = getCurrentUserId(auth);
        try {
            cartService.removeFromCart(userId, req.getProductId(), req.getQuantity());
            return ResponseEntity.ok(Map.of("removed", true));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/clear")
    public ResponseEntity<?> clearCart(Authentication auth) {
        Long userId = getCurrentUserId(auth);
        cartService.clearCart(userId);
        return ResponseEntity.ok(Map.of("cleared", true));
    }

    public static class AddReq {
        private Long productId;
        private Integer quantity;

        public Long getProductId(){ return productId; }
        public void setProductId(Long productId){ this.productId = productId; }
        public Integer getQuantity(){ return quantity; }
        public void setQuantity(Integer quantity){ this.quantity = quantity; }
    }
}
