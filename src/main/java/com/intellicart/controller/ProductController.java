package com.intellicart.controller;

import com.intellicart.model.Product;
import com.intellicart.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService svc;

    public ProductController(ProductService svc) {
        this.svc = svc;
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product p) {
        Product saved = svc.createProduct(p);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
    return svc.getProduct(id)
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(404).body("Product not found"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product update) {
        try {
            Product p = svc.updateProduct(id, update);
            return ResponseEntity.ok(p);
        } catch (IllegalArgumentException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(404).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        svc.deleteProduct(id);
        return ResponseEntity.ok(Map.of("deleted", id));
    }

    @GetMapping
    public ResponseEntity<List<Product>> listAll(@RequestParam(required = false) String category) {
        if (category != null) return ResponseEntity.ok(svc.findByCategory(category));
        return ResponseEntity.ok(svc.listAll());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchByName(@RequestParam String q) {
        var opt = svc.searchByName(q);
        return ResponseEntity.ok(opt);
    }
}
