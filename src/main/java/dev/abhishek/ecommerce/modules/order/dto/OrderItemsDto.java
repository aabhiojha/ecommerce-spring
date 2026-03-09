package dev.abhishek.ecommerce.modules.order.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemsDto {

    private Long id;
    private Long quantity;
    private BigDecimal price_at_purchase;
    private Long order_id;
    private String productId;
    private String productName;
    private String productBrand;
    private String productDescription;
    private String productImage;
    private BigDecimal sub_total;

}
