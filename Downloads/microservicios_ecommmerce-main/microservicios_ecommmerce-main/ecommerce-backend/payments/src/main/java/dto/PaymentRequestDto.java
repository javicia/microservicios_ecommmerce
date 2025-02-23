package dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PaymentRequestDto {

	private String orderId;
	private BigDecimal amount;
	private String currency;
}
