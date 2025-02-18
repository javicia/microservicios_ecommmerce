package kafka;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.OrderStatus;
import model.Orders;
import service.IOrderService;

@Component
public class CartEventListener {

	private static final Logger logger = LoggerFactory.getLogger(CartEventListener.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private IOrderService orderService;

	// Escucha eventos del tópico "cart-events"
	@KafkaListener(topics = "cart-events", groupId = "orders_group")
	public void handelCartEvent(String message) {
		try {
			String[] parts = message.split(":", 2);
			String eventType = parts[0];
			String cartJson = parts[1];

			if ("CART_SUBMITTED".equals(eventType)) {
				// Aquí mapea los datos del carrito a un objeto de pedido (Orders)
				// Por ejemplo, se crea un pedido a partir del carrito
				Orders order = convertCartToOrder(cartJson);
				Orders createdOrder = orderService.createOrder(order);
				logger.info("Pedido creado a partir del carrito: ID {}", createdOrder.getId());
			} else {
				logger.warn("Evento de carrito desconocido: {}", eventType);
			}
		} catch (Exception e) {
			logger.error("Error al procesar evento de carrito", e);
		}
	}

	private Orders convertCartToOrder(String cartJson) {
		// Aquí deberías implementar la lógica de conversión de carrito a pedido.
		Orders order = new Orders();
		// Asigna JSON o mapea los datos del carrito al pedido
		order.setStatus(OrderStatus.CREATED);
		order.setOrderDate(LocalDateTime.now());
		return order;
	}
}
