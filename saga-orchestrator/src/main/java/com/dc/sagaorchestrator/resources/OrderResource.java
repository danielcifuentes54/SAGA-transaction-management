package com.dc.sagaorchestrator.resources;

import com.dc.sagaorchestrator.models.Order;
import com.dc.sagaorchestrator.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderResource {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity placeOrder(@RequestBody Order order) throws URISyntaxException {
        Order savedOrder = orderService.placeOrder(order);
        return ResponseEntity.created(new URI(savedOrder.getOrderId().toString())).build();
    }
}
