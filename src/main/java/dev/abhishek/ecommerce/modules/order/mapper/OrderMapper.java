package dev.abhishek.ecommerce.modules.order.mapper;

import dev.abhishek.ecommerce.modules.order.dto.OrderDto;
import dev.abhishek.ecommerce.modules.order.dto.OrderItemDto;
import dev.abhishek.ecommerce.modules.order.entity.Order;
import dev.abhishek.ecommerce.modules.order.entity.OrderItem;
import dev.abhishek.ecommerce.modules.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    // must convert user to user_id
    @Mapping(source = "user", target = "user_id")
    // List of orderItems to total price
    @Mapping(source = "orderItems", target = "total_price")
    // List of orderItems to orderItemsDto
    @Mapping(source = "orderItems", target = "orderItems")
    OrderDto toOrderDto(Order order);


    List<OrderDto> toOrderDtoList(List<Order> orders);


    // OrderItem to OrderItemDto
    // order to order_id
    @Mapping(source = "order", target = "order_id")
    // Product product to Long productId
    @Mapping(source = "product", target = "productId")
    // Product product to String productName
    @Mapping(source = "product", target = "productName")
    @Mapping(source = "product", target = "productBrand")
    @Mapping(source = "product", target = "productDescription")
    // product obj to String productImage download url tbp
    @Mapping(source = "product", target = "productImage")
    @Mapping(source = ".", target = "sub_total")
    OrderItemDto toOrderItemDto(OrderItem orderItem);

    List<OrderItemDto> toOrderItemDtoList(List<OrderItem> orderItems);


    // user to return user_id
    default Long mapUserToUser_id(User user){
        return user.getId();
    }

//    default BigDecimal mapOrderItemsToTotalPrice(List<OrderItem> orderItems){}


}
