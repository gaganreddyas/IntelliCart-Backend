package com.intellicart.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "products", indexes = {
        @Index(columnList = "name", name = "idx_products_name"),
        @Index(columnList = "category", name = "idx_products_category")
})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    private String category;

    @Column(nullable=false)
    private Double price;

    @Column(nullable=false)
    private Integer stockQuantity;

    @Column(nullable=false)
    private Instant createdAt = Instant.now();

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name;}
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category;}
    public void setCategory(String category) { this.category = category; }

    public Double getPrice() { return price;}
    public void setPrice(Double price) { this.price = price; }

    public Integer getStockQuantity() { return stockQuantity;}
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
