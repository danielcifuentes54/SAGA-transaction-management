package com.dc.hotelmicroservice.repositories;

import com.dc.hotelmicroservice.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
