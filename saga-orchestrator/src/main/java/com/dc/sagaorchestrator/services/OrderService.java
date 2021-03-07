package com.dc.sagaorchestrator.services;

import com.dc.sagaorchestrator.domain.OrderEvent;
import com.dc.sagaorchestrator.domain.OrderState;
import com.dc.sagaorchestrator.domain.OrderStatus;
import com.dc.sagaorchestrator.models.Order;
import com.dc.sagaorchestrator.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;
    private final OrderStateChangeInterceptor orderStateChangeInterceptor;

    @Transactional
    public Order placeOrder(Order order) {
        order.setOrderId(null); // defensive approach for new object
        order.setOrderState(OrderState.NEW);
        order.setOrderStatus(OrderStatus.NEW);
        Order savedOrder = orderRepository.saveAndFlush(order);
        System.out.println("$$$$ orderId " + order.getOrderId() + " $$$$");

        sendOrderEvent(order, OrderEvent.BOOK_AIRLINE);
        return savedOrder;
    }


    private void sendOrderEvent(Order order, OrderEvent bookingEvent) {
        StateMachine<OrderState, OrderEvent> sm = build(order);
        Message<OrderEvent> msg = MessageBuilder.withPayload(bookingEvent)
                .setHeader("ORDER_ID_HEADER", order.getOrderId().toString())
                .build();

        sm.sendEvent(msg);
    }

    private StateMachine<OrderState, OrderEvent> build(Order order) {
        StateMachine<OrderState, OrderEvent> sm = stateMachineFactory.getStateMachine(String.valueOf(order.getOrderId()));
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(orderStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(order.getOrderState(), null, null, null));
                });
        sm.start();
        return sm;
    }

    @Transactional
    public void processSagaResponse(Order order) {
        if(order.getOrderStatus().name().equals("AIRLINE_SUCCESS")) {
            order.setOrderState(OrderState.AIRLINE);
            Order savedOrder = orderRepository.saveAndFlush(order);
            sendOrderEvent(order, OrderEvent.BOOK_AIRLINE_COMPLETED);
        } else if(order.getOrderStatus().name().equals("HOTEL_SUCCESS")) {
            order.setOrderState(OrderState.COMPLETED);
            order.setOrderStatus(OrderStatus.COMPLETED);
            Order savedOrder = orderRepository.saveAndFlush(order);

        } else if(order.getOrderStatus().name().equals("HOTEL_FAILED")) {
            order.setOrderState(OrderState.HOTEL);
            Order savedOrder = orderRepository.saveAndFlush(order);
            sendOrderEvent(order, OrderEvent.BOOK_HOTEL_FAILED);
        } else if(order.getOrderStatus().name().equals("FAILED")) {
            System.out.println("BOOKING FAILED");
        }
    }
}
