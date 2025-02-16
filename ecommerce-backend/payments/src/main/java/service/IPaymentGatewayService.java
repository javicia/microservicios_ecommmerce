package service;

import org.springframework.stereotype.Service;

import dto.PaymentRequestDto;
import dto.PaymentResponseDto;
import model.PaymentStatus;

@Service
public class IPaymentGatewayService {

	public PaymentResponseDto processPayment(PaymentRequestDto request) {
		PaymentResponseDto response = new PaymentResponseDto();
		response.setStatus(PaymentStatus.COMPLETED);
		response.setTransactionId("TXT-" + System.currentTimeMillis());
		return response;
	}

}
