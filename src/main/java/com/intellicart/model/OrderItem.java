package com.intellicart.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @ManyToOne(optional=false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable=false)
    private Integer quantity;

    @Column(nullable=false)
    private Double priceAtPurchase;

    // getters/setters
    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }

    public OrderEntity getOrder(){ return order; }
    public void setOrder(OrderEntity order){ this.order = order; }

    public Product getProduct(){ return product; }
    public void setProduct(Product product){ this.product = product; }

    public Integer getQuantity(){ return quantity; }
    public void setQuantity(Integer quantity){ this.quantity = quantity; }

    public Double getPriceAtPurchase(){ return priceAtPurchase; }
    public void setPriceAtPurchase(Double priceAtPurchase){ this.priceAtPurchase = priceAtPurchase; }
}
