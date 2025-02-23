package dto;

import lombok.Data;
import model.PaymentStatus;

@Data
public class PaymentResponseDto {

	private String transactionId;
	private PaymentStatus status;

}
