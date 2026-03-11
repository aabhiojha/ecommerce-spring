package dev.abhishek.ecommerce.modules.review.mapper;

import dev.abhishek.ecommerce.modules.product.entity.Product;
import dev.abhishek.ecommerce.modules.review.dto.ReviewDto;
import dev.abhishek.ecommerce.modules.review.entity.Review;
import dev.abhishek.ecommerce.modules.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    @Mapping(source = "user", target = "userId", qualifiedByName = "userToUserId")
    @Mapping(source = "user", target = "userName", qualifiedByName = "userToUserName")
    @Mapping(source = "product", target = "productId", qualifiedByName = "productToProductId")
    @Mapping(source = "product", target = "productName", qualifiedByName = "productToProductName")
    ReviewDto toDto(Review review);

    List<ReviewDto> toDtoList(List<Review> reviews);

    @Named("userToUserId")
    default Long mapUserToUserId(User user){
        return user.getId();
    }

    @Named("userToUserName")
    default String mapUserToUserName(User user){
        return user.getUsername();
    }

    @Named("productToProductId")
    default Long mapProductToProductId(Product product){
        return product.getId();
    }

    @Named("productToProductName")
    default String mapProductToProductName(Product product){
        return product.getName();
    }
}
