package com.intellicart.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String orderRef; // human-friendly order id like ORD-...

    @ManyToOne(optional=false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable=false)
    private Instant createdAt = Instant.now();

    @Column(name = "total_amount", nullable=false)
    private Double totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    // getters/setters
    public Long getId(){ return id;}
    public void setId(Long id){ this.id = id; }

    public String getOrderRef(){ return orderRef; }
    public void setOrderRef(String orderRef){ this.orderRef = orderRef; }

    public User getUser(){ return user; }
    public void setUser(User user){ this.user = user; }

    public Instant getCreatedAt(){ return createdAt; }
    public void setCreatedAt(Instant createdAt){ this.createdAt = createdAt; }

    public Double getTotalAmount(){ return totalAmount; }
    public void setTotalAmount(Double totalAmount){ this.totalAmount = totalAmount; }

    public List<OrderItem> getItems(){ return items; }
    public void setItems(List<OrderItem> items){ this.items = items; }
}
