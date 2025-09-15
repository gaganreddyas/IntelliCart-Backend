package com.intellicart.service;

import com.intellicart.model.CartItem;
import com.intellicart.model.Product;
import com.intellicart.model.User;
import com.intellicart.repository.CartItemRepository;
import com.intellicart.repository.ProductRepository;
import com.intellicart.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {
    private final CartItemRepository cartRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final ProductService productService;

    public CartService(CartItemRepository cartRepo, UserRepository userRepo, ProductRepository productRepo, ProductService productService) {
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.productService = productService;
    }

    public List<CartItem> getCartForUser(Long userId) {
        var u = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return cartRepo.findByUser(u);
    }

    /**
     * Add item to cart: decrement stock immediately (per defaults). If cart already has item, increase quantity.
     * Uses transaction to ensure stock consistency.
     */
    @Transactional
    public CartItem addToCart(Long userId, Long productId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");
        var user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        var product = productRepo.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // decrement stock via productService (locks)
        productService.decrementStock(productId, quantity);

        // update or create cart item
        var existing = cartRepo.findByUserIdAndProductId(userId, productId);
        CartItem item;
        if (existing.isPresent()) {
            item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setQuantity(quantity);
        }
        return cartRepo.save(item);
    }

    @Transactional
    public void removeFromCart(Long userId, Long productId, int quantity) {
        var existing = cartRepo.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");
        if (quantity >= existing.getQuantity()) {
            // remove item and restore stock
            cartRepo.delete(existing);
            productService.incrementStock(productId, existing.getQuantity());
        } else {
            existing.setQuantity(existing.getQuantity() - quantity);
            cartRepo.save(existing);
            productService.incrementStock(productId, quantity);
        }
    }

    @Transactional
    public void clearCart(Long userId) {
        var user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        var items = cartRepo.findByUser(user);
        for (var it : items) {
            productService.incrementStock(it.getProduct().getId(), it.getQuantity());
        }
        cartRepo.deleteByUserId(userId);
    }
}
