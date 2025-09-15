package com.intellicart.service;

import com.intellicart.repository.OrderRepository;
import com.intellicart.repository.ProductRepository;
import com.intellicart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AnalyticsService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> getDashboardAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalSales", orderRepository.totalSalesAllTime());
        // Top products: just return all for now
        List<Map<String, Object>> topProducts = new ArrayList<>();
        productRepository.findAll().forEach(p -> {
            Map<String, Object> prod = new HashMap<>();
            prod.put("productId", p.getId());
            prod.put("name", p.getName());
            prod.put("category", p.getCategory());
            prod.put("stock", p.getStockQuantity());
            topProducts.add(prod);
        });
        analytics.put("topSellingProducts", topProducts);
        // Most active users: by number of orders
        Map<String, Integer> userOrderCounts = new HashMap<>();
        userRepository.findAll().forEach(u -> {
            int count = orderRepository.findByUserId(u.getId()).size();
            userOrderCounts.put(u.getEmail(), count);
        });
        analytics.put("mostActiveUsers", userOrderCounts);
        return analytics;
    }
}