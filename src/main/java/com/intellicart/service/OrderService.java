package com.intellicart.service;

import com.intellicart.model.*;
import com.intellicart.repository.CartItemRepository;
import com.intellicart.repository.OrderRepository;
import com.intellicart.repository.ProductRepository;
import com.intellicart.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderService {
    private final UserRepository userRepo;
    private final CartItemRepository cartRepo;
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;

    public OrderService(UserRepository userRepo, CartItemRepository cartRepo, OrderRepository orderRepo, ProductRepository productRepo) {
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    private String generateOrderRef() {
        String ts = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int rand = new Random().nextInt(9000) + 1000;
        return "ORD-" + ts + "-" + rand;
    }

    /**
     * Place order: wraps in transaction, pulls cart items, double-checks stock (using PESSIMISTIC_WRITE via repository),
     * deducts stock (already deducted on add-to-cart, but we re-verify and ensure final consistency), creates order and items,
     * clears cart. If any step fails, rollback occurs.
     */
    @Transactional
    public OrderEntity placeOrder(Long userId) {
        var user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        var cartItems = cartRepo.findByUser(user);
        if (cartItems.isEmpty()) throw new IllegalStateException("Cart is empty");

        // verify product stocks and prepare order items
        double total = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem ci : cartItems) {
            Product p = productRepo.findByIdForUpdate(ci.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            if (p.getStockQuantity() < 0) { // defensive
                throw new IllegalStateException("Inconsistent stock for product " + p.getName());
            }
            // Note: stock was already decremented at add-to-cart. We do not double-decrement here.
            OrderItem oi = new OrderItem();
            oi.setProduct(p);
            oi.setQuantity(ci.getQuantity());
            oi.setPriceAtPurchase(p.getPrice());
            orderItems.add(oi);
            total += p.getPrice() * ci.getQuantity();
        }

        // create OrderEntity
        OrderEntity order = new OrderEntity();
        order.setOrderRef(generateOrderRef());
        order.setUser(user);
        order.setTotalAmount(total);
        order.setCreatedAt(new Date().toInstant());

        // attach items to order
        for (OrderItem oi : orderItems) {
            oi.setOrder(order);
            order.getItems().add(oi);
        }

        orderRepo.save(order);

        // clear cart (stock already adjusted earlier)
        cartRepo.deleteByUserId(userId);

        return order;
    }

    public List<OrderEntity> getOrdersForUser(Long userId) {
        return orderRepo.findByUserId(userId);
    }

    public Double totalSales() {
        Double v = orderRepo.totalSalesAllTime();
        return v == null ? 0.0 : v;
    }
}
