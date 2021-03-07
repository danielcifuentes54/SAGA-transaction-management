package com.dc.hotelmicroservice.models;

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
    private Long hotelOrderId;

    private Long orderId;

    private String orderState;

    private String orderStatus;

    private String sourceLocation;

    private String destinationLocation;

    private BigDecimal amount;

    private LocalDate bookingDate;
}
