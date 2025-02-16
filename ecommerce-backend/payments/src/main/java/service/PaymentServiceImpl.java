package service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dto.PaymentRequestDto;
import model.Payment;
import model.PaymentStatus;
import repository.IPaymentRepository;

@Service
public class PaymentServiceImpl implements IPaymentService {

	@Autowired
	private IPaymentRepository paymentRepository;

	@Autowired
	private IPaymentGatewayService paymentGatewayService;

	@Override
	public Payment processPayment(Payment payment) {
		// Procesa el pago
		payment.setPaymentDate(LocalDateTime.now());

		// Crear un DTO de solicitud para la pasarela de pago
		PaymentRequestDto request = new PaymentRequestDto();
		request.setOrderId(payment.getOrderId());
		request.setAmount(payment.getAmount());
		request.setCurrency("USD");

		// Procesa el pago con la pasarela de pago
		var response = paymentGatewayService.processPayment(request);
		payment.setStatus(response.getStatus());
		payment.setTransactionId(response.getTransactionId());

		return paymentRepository.save(payment);
	}

	@Override
	public Payment getPaymentById(Long id) {
		return paymentRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Pago no encontrado con id: " + id));
	}

	@Override
	public List<Payment> getAllPayments() {
		return paymentRepository.findAll();
	}

	@Override
	public Payment updatePayment(Long id, Payment payment) {
		Payment existingPayment = getPaymentById(id);
		// Actualiza el monto y el estado del pago
		existingPayment.setAmount(payment.getAmount());
		existingPayment.setStatus(payment.getStatus());
		return paymentRepository.save(existingPayment);
	}

	@Override
	public void cancelPayment(Long id) {
		Payment payment = getPaymentById(id);
		// Cancela el pago
		payment.setStatus(PaymentStatus.CANCELLED);
		paymentRepository.save(payment);

	}

}
