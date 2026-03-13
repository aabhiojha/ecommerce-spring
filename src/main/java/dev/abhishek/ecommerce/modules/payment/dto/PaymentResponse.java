package dev.abhishek.ecommerce.modules.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long paymentId;
    private String checkoutSessionId;
    private String checkoutUrl;
    private String paymentIntentId;
    private String clientSecret;
    private String status;
    private Long amount;
    private String currency;
    private UUID orderId;
}
