package service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.OrderStatus;
import model.Orders;
import repository.IOrderRepository;

@Service
public class OrderServiceImpl implements IOrderService {

	@Autowired
	private IOrderRepository orderRepository;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Método para “submittar” el carrito (finalizar compra). Publica el evento
	 * "CART_SUBMITTED" en Kafka.
	 */
	@Override
	public Orders createOrder(Orders order) {
		order.setOrderDate(LocalDateTime.now());
		order.setStatus(OrderStatus.CREATED);

		// Cálculo del total basado en los ítems del pedido

		if (order.getItems() != null) {
			BigDecimal total = order.getItems().stream()
					.map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			order.setTotal(total);
		}
		Orders savedOrder = orderRepository.save(order);

		// Publica el evento ORDER_CREATED solo si hay ítems (o según tu lógica de
		// negocio)
		if (order.getItems() != null && !order.getItems().isEmpty()) {
			publishOrderEvent("ORDER_CREATED", order);
		}
		return savedOrder;
	}

	@Override
	public Orders getOrderById(Long id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Pedido no encontrado con id: " + id));
	}

	@Override
	public List<Orders> getAllOrders() {
		return orderRepository.findAll();
	}

	@Override
	public Orders updateOrder(Long id, Orders order) {
		Orders existinOrder = getOrderById(id);
		// Actualiza los ítems y recalcula el total
		if (order.getItems() != null) {
			BigDecimal total = order.getItems().stream()
					.map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			existinOrder.setTotal(total);
		}
		// Permite actualizar el estado si se envía en la petición
		if (order.getStatus() != null) {
			existinOrder.setStatus(order.getStatus());
		}
		return orderRepository.save(existinOrder);
	}

	@Override
	public void deleteOrder(Long id) {
		Orders order = getOrderById(id);
		order.setStatus(OrderStatus.CANCELLED);
		orderRepository.save(order);

	}

	// Método que publica eventos de Kafka
	public void publishOrderEvent(String eventType, Orders order) {
		try {
			String orderJson = objectMapper.writeValueAsString(order);
			String message = eventType + ":" + orderJson;
			kafkaTemplate.send("order-events", message);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error al convertir el pedido a JSON", e);
		}
	}

}
