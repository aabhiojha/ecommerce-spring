package dev.abhishek.ecommerce.modules.cart.mapper;

import dev.abhishek.ecommerce.modules.Image.entity.Image;
import dev.abhishek.ecommerce.modules.cart.dto.cart.CartDto;
import dev.abhishek.ecommerce.modules.cart.dto.cartItem.CartItemDto;
import dev.abhishek.ecommerce.modules.cart.entity.Cart;
import dev.abhishek.ecommerce.modules.cart.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CartMapper {

    // cart to cartDto

    //    1. conversion from List<CartItem> to List<cartItemDto>
    @Mapping(source = "cartItems", target = "items")
//    2. conversion from List<CartItem> to Long
    @Mapping(source = "cartItems", target = "totalItems")
//    3. Conversion from List<CartItem> to BigDecimal
    @Mapping(source = "cartItems", target = "totalPrice")
    CartDto toDto(Cart cart);

    //    1. List<cart> to List<cartDto>
    List<CartDto> toDtoList(List<Cart> carts);


    // cartItem to cartItemDto

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
//    Get DownloadUrl from product images list
    @Mapping(source = "product.images", target = "productImage")
    @Mapping(source = "product.price", target = "price")
    @Mapping(source = "quantity", target = "quantity")

//    map item to subtotal
    @Mapping(source = ".", target = "subtotal")
    CartItemDto toItemDto(CartItem cartItem);

    // List<cartItem> to List<cartItemDto>
    List<CartItemDto> toItemDtoList(List<CartItem> cartItems);

    // helper functions absolutely necessary for this shit to work

    //    2. conversion from List<CartItem> to Long
    default Long mapCartItemsToTotalItems(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) return 0L;

        return cartItems.stream()
                .map(cartItem -> cartItem.getQuantity())
                .filter(quantity -> quantity != null)
                .reduce(0L, (a, b) -> a + b);
    }

    //    3. Conversion from List<CartItem> to BigDecimal
    default BigDecimal mapCartItemsToTotalPrice(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) return BigDecimal.ZERO;

        return cartItems.stream()
                .map(cartItem -> mapCartItemToSubTotal(cartItem))
                .reduce(BigDecimal.ZERO, (a,b)-> a.add(b));
    }

    default BigDecimal mapCartItemToSubTotal(CartItem cartItem) {
        if (cartItem == null || cartItem.getProduct() == null || cartItem.getProduct().getPrice() == null) {
            return BigDecimal.ZERO;
        }
        return cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }

    default String mapImagesToFirstProductImage(List<Image> images){
        if(images == null || images.isEmpty() || images.get(0) == null) return null;

        return images.getFirst().getDownloadUrl();
    }
}
