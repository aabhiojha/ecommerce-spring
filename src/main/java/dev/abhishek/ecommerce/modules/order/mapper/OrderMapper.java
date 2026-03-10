package dev.abhishek.ecommerce.modules.order.mapper;

import dev.abhishek.ecommerce.modules.Image.entity.Image;
import dev.abhishek.ecommerce.modules.order.dto.OrderDto;
import dev.abhishek.ecommerce.modules.order.dto.OrderItemDto;
import dev.abhishek.ecommerce.modules.order.entity.Order;
import dev.abhishek.ecommerce.modules.order.entity.OrderItem;
import dev.abhishek.ecommerce.modules.product.entity.Product;
import dev.abhishek.ecommerce.modules.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(source = "user", target = "user_id", qualifiedByName = "userToId")
    @Mapping(source = "orderItems", target = "total_price", qualifiedByName = "orderItemsToTotalPrice")
    OrderDto toOrderDto(Order order);

    List<OrderDto> toOrderDtoList(List<Order> orders);

    @Mapping(source = "order.id", target = "order_id")
    @Mapping(source = "product", target = "productId", qualifiedByName = "productToId")
    @Mapping(source = ".", target = "productName", qualifiedByName = "orderItemToProductName")
    @Mapping(source = ".", target = "productBrand", qualifiedByName = "orderItemToProductBrand")
    @Mapping(source = ".", target = "productDescription", qualifiedByName = "orderItemToProductDescription")
    @Mapping(source = ".", target = "productImage", qualifiedByName = "orderItemToProductImage")
    @Mapping(source = ".", target = "sub_total", qualifiedByName = "orderItemToSubTotal")
    OrderItemDto toOrderItemDto(OrderItem orderItem);

    List<OrderItemDto> toOrderItemDtoList(List<OrderItem> orderItems);

    @Named("userToId")
    default Long mapUserToUserId(User user) {
        return user == null ? null : user.getId();
    }

    @Named("orderItemsToTotalPrice")
    default BigDecimal mapOrderItemsToTotalPrice(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return orderItems.stream()
                .map(this::mapOrderItemToSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Named("productToId")
    default String mapProductToProductId(Product product) {
        if (product == null || product.getId() == null) {
            return null;
        }

        return String.valueOf(product.getId());
    }

    @Named("orderItemToProductName")
    default String mapOrderItemToProductName(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        if (orderItem.getProductName() != null) {
            return orderItem.getProductName();
        }

        Product product = orderItem.getProduct();
        return product == null ? null : product.getName();
    }

    @Named("orderItemToProductBrand")
    default String mapOrderItemToProductBrand(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        if (orderItem.getProductBrand() != null) {
            return orderItem.getProductBrand();
        }

        Product product = orderItem.getProduct();
        return product == null ? null : product.getBrand();
    }

    @Named("orderItemToProductDescription")
    default String mapOrderItemToProductDescription(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        if (orderItem.getProductDescription() != null) {
            return orderItem.getProductDescription();
        }

        Product product = orderItem.getProduct();
        return product == null ? null : product.getDescription();
    }

    @Named("orderItemToProductImage")
    default String mapOrderItemToProductImage(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        if (orderItem.getProductImage() != null) {
            return orderItem.getProductImage();
        }

        Product product = orderItem.getProduct();
        if (product == null) {
            return null;
        }

        return mapImagesToFirstProductImage(product.getImages());
    }

    @Named("orderItemToSubTotal")
    default BigDecimal mapOrderItemToSubTotal(OrderItem orderItem) {
        if (orderItem == null || orderItem.getPrice_at_purchase() == null || orderItem.getQuantity() == null) {
            return BigDecimal.ZERO;
        }

        return orderItem.getPrice_at_purchase().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
    }

    default String mapImagesToFirstProductImage(List<Image> images) {
        if (images == null || images.isEmpty() || images.getFirst() == null) {
            return null;
        }

        return images.getFirst().getDownloadUrl();
    }

    default Date map(LocalDateTime value) {
        if (value == null) {
            return null;
        }

        return Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
    }
}
