package dev.abhishek.ecommerce.modules.order.dto;

import dev.abhishek.ecommerce.modules.order.misc.StatusChoice;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private UUID id;
    private StatusChoice status;
    private Long user_id;
    private List<OrderItemDto> orderItems;
    private BigDecimal total_price;
    private Date created_at;
    private Date updated_at;
}
