package com.dc.sagaorchestrator.services;

import com.dc.sagaorchestrator.domain.OrderEvent;
import com.dc.sagaorchestrator.domain.OrderState;
import com.dc.sagaorchestrator.models.Order;
import com.dc.sagaorchestrator.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStateChangeInterceptor extends StateMachineInterceptorAdapter<OrderState, OrderEvent> {

    private final OrderRepository orderRepository;

    @Override
    public void preStateChange(State<OrderState, OrderEvent> state, Message<OrderEvent> message, Transition<OrderState, OrderEvent> transition, StateMachine<OrderState, OrderEvent> stateMachine) {
        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault("ORDER_ID_HEADER", "")))
                .ifPresent(orderId -> {
                    log.debug("Saving state for order id: " + orderId + " Status: " + state.getId());
                    Order order = orderRepository.getOne(Long.parseLong(orderId));
                    order.setOrderState(state.getId());
                    orderRepository.saveAndFlush(order);// Hibernate is LazyLoading, so to avoid that.
                });
    }
}
