package com.intellicart.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items", indexes = {
        @Index(columnList = "user_id", name = "idx_cart_user")
})
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional=false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable=false)
    private Integer quantity;

    // getters/setters
    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }

    public User getUser(){ return user; }
    public void setUser(User user){ this.user = user; }

    public Product getProduct(){ return product; }
    public void setProduct(Product product){ this.product = product; }

    public Integer getQuantity(){ return quantity; }
    public void setQuantity(Integer quantity){ this.quantity = quantity; }
}
