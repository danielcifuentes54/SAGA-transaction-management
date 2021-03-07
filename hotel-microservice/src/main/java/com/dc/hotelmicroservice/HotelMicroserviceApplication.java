package com.dc.hotelmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.dc.hotelmicroservice.models.Order;
import com.dc.hotelmicroservice.repositories.OrderRepository;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableJms
public class HotelMicroserviceApplication {

	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private Queue sagaQueue;
	@Bean
	public Queue sagaQueue() {
		return new ActiveMQQueue("saga-queue");
	}

	@Bean
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		Map<String, Class<?>> typeIdMappings = new HashMap<String, Class<?>>();
		typeIdMappings.put("JMS_TYPE", Order.class);

		converter.setTypeIdMappings(typeIdMappings);
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}

	@Bean
	public JmsListenerContainerFactory<?> jsaFactory(ConnectionFactory connectionFactory,
													 DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setMessageConverter(jacksonJmsMessageConverter());
		configurer.configure(factory, connectionFactory);
		return factory;
	}

	@JmsListener(destination = "hotel-queue")
	public void listen(Order order) {
		System.out.println("Message Consumed: " + order);
		if (order.getOrderId() % 2 == 0) {
			order.setOrderStatus("HOTEL_FAILED");
			// initiate airline compensate
			orderRepository.save(order);
		} else {
			order.setOrderStatus("HOTEL_SUCCESS");
			// finalize booking
			orderRepository.save(order);
		}
		jmsTemplate.convertAndSend(sagaQueue, order);
	}

	public static void main(String[] args) {
		SpringApplication.run(HotelMicroserviceApplication.class, args);
	}

}
