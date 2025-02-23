package kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OrderEventPublisher {

	private static final Logger logger = LoggerFactory.getLogger(OrderEventPublisher.class);

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	public void publishOrderEvent(String eventType, String order) {
		try {
			// "ORDER_CREATED:{...json...}"
			String message = eventType + ":" + objectMapper.writeValueAsString(order);
			kafkaTemplate.send("order-events", order);
			logger.info("Evento de pedido publicado: {}", message);
		} catch (Exception e) {
			logger.error("Error al publicar evento de pedido", e);
		}
	}
}
