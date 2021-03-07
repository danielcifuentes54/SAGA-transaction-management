package com.dc.airlinemicroservice.repositories;

import com.dc.airlinemicroservice.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
