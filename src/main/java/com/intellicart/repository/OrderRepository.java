package com.intellicart.repository;

import com.intellicart.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByUserId(Long userId);

    @Query("SELECT SUM(o.totalAmount) FROM OrderEntity o")
    Double totalSalesAllTime();
}
