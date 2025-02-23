package controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import model.Payment;
import service.IPaymentService;

@RestController
@Tag(name = "Pagos", description = "Endpoints para procesar y gestionar pagos")
public class PaymentController {

	@Autowired
	private IPaymentService paymentService;

	@Operation(summary = "Procesar pago", description = "Procesa un nuevo pago y retorna la información del pago procesado")
	@PostMapping
	public ResponseEntity<Payment> processPayment(@RequestBody Payment payment) {
		Payment processedPayment = paymentService.processPayment(payment);
		return new ResponseEntity<>(processedPayment, HttpStatus.CREATED);
	}

	// Obtener pago por ID: Mapea a GET /payments/{id}
	@Operation(summary = "Obtener pago", description = "Recupera la información de un pago por su ID")
	@GetMapping("/{id}")
	public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
		Payment payment = paymentService.getPaymentById(id);
		return ResponseEntity.ok(payment);
	}

	// Listar todos los pagos: Mapea a GET /payments
	@Operation(summary = "Listar pagos", description = "Lista todos los pagos realizados")
	@GetMapping
	public ResponseEntity<List<Payment>> getAllPayments() {
		List<Payment> payments = paymentService.getAllPayments();
		return ResponseEntity.ok(payments);
	}

	// Actualizar pago: Mapea a PUT /payments/{id}
	@Operation(summary = "Actualizar pago", description = "Actualiza la información de un pago existente")
	@PutMapping("/{id}")
	public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody Payment payment) {
		Payment updatedPayment = paymentService.updatePayment(id, payment);
		return ResponseEntity.ok(updatedPayment);
	}

	// Cancelar pago: Mapea a DELETE /payments/{id}
	@Operation(summary = "Cancelar pago", description = "Cancela un pago existente")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> cancelPayment(@PathVariable Long id) {
		paymentService.cancelPayment(id);
		return ResponseEntity.noContent().build();
	}
}
