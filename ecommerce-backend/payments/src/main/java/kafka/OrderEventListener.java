package kafka;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.Payment;
import model.PaymentStatus;
import service.IPaymentService;

@Component
public class OrderEventListener {

	private static final Logger logger = LoggerFactory.getLogger(OrderEventListener.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private IPaymentService paymentService;

	// Escucha eventos del tópico "order-events"
	@KafkaListener(topics = "order-events", groupId = "payments_group")
	public void handleOrderEvent(String message) {
		try {
			// Se espera formato "EVENT_TYPE:json"
			String[] parts = message.split(":", 2);
			String eventType = parts[0];
			String orderJson = parts[1];

			if ("ORDER_CREATED".equals(eventType)) {
				// Convertir el JSON a un objeto Orders (o extraer el orderId y monto)
				// Aquí asumiremos que obtenemos el orderId y total
				// Para el ejemplo, se crea un Payment básico:
				Payment payment = new Payment();
				// Aquí asigna los campos necesarios, por ejemplo:
				// payment.setOrderId(...);
				// payment.setAmount(...);
				payment.setPaymentDate(LocalDateTime.now());
				payment.setStatus(PaymentStatus.PENDING);
				Payment processedPayment = paymentService.processPayment(payment);
				logger.info("Pago creado a partir del pedido: ID {}", processedPayment.getId());
			} else {
				logger.warn("Evento de pedido desconocido: {}", eventType);
			}
		} catch (Exception e) {
			logger.error("Error al procesar evento de pedido", e);
		}
	}

}
