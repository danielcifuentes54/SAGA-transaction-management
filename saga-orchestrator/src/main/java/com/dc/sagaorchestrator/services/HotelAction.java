package com.dc.sagaorchestrator.services;

import com.dc.sagaorchestrator.domain.OrderEvent;
import com.dc.sagaorchestrator.domain.OrderState;
import com.dc.sagaorchestrator.models.Order;
import com.dc.sagaorchestrator.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HotelAction implements Action<OrderState, OrderEvent> {
    private final JmsTemplate jmsTemplate;
    private final OrderRepository orderRepository;


    @Override
    public void execute(StateContext<OrderState, OrderEvent> context) {

        String orderId = (String) context.getMessage().getHeaders().get("ORDER_ID_HEADER");
        Optional<Order> orderOptional = orderRepository.findById(Long.parseLong(orderId));
        jmsTemplate.convertAndSend("hotel-queue", orderOptional.get());
    }
}
