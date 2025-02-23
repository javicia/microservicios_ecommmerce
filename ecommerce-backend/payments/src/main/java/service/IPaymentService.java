package service;

import java.util.List;

import model.Payment;

public interface IPaymentService {

	Payment processPayment(Payment payment);

	Payment getPaymentById(Long id);

	List<Payment> getAllPayments();

	Payment updatePayment(Long id, Payment payment);

	void cancelPayment(Long id);

}
