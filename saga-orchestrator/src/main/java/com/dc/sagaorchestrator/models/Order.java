package com.dc.sagaorchestrator.models;

import com.dc.sagaorchestrator.domain.OrderState;
import com.dc.sagaorchestrator.domain.OrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    private OrderState orderState;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private String sourceLocation;

    private String destinationLocation;

    private BigDecimal amount;

    private LocalDate bookingDate;
}
