package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Identificador del pedido al que corresponde el pago
	private String orderId;

	// Monto del pago
	private BigDecimal amount;

	// Fecha y hora en que se procesó el pago
	private LocalDateTime paymentDate;

	// Estado del pago (PENDING, COMPLETED, FAILED, CANCELLED)
	@Enumerated(EnumType.STRING)
	private PaymentStatus status;

	// (Opcional) Id de transacción simulado
	private String transactionId;
}
