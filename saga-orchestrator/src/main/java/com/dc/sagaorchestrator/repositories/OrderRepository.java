package com.dc.sagaorchestrator.repositories;

import com.dc.sagaorchestrator.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
