package com.dc.sagaorchestrator.services;

import com.dc.sagaorchestrator.models.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SagaJmsListener {

    private final JmsTemplate jmsTemplate;
    private final OrderService orderService;

    @JmsListener(destination = "saga-queue")
    public void listen(Order order) {
        orderService.processSagaResponse(order);
    }
}
