package dev.abhishek.ecommerce.modules.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLineItemSnapshot {
    private Long cartItemId;
    private Long productId;
    private Long quantity;
    private BigDecimal unitPrice;
    private String productName;
    private String productBrand;
    private String productDescription;
    private String productImage;
}
