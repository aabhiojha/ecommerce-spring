package dev.abhishek.ecommerce.modules.order.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
     private List<Long> cartItemIds;
}
