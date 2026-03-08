package dev.abhishek.ecommerce.modules.product.mapper;

import dev.abhishek.ecommerce.modules.Image.dtos.ImageDto;
import dev.abhishek.ecommerce.modules.Image.entity.Image;
import dev.abhishek.ecommerce.modules.Image.mapper.ImageMapper;
import dev.abhishek.ecommerce.modules.category.entity.Category;
import dev.abhishek.ecommerce.modules.product.dto.CreateProductRequest;
import dev.abhishek.ecommerce.modules.product.dto.ProductDto;
import dev.abhishek.ecommerce.modules.product.dto.UpdateProductRequest;
import dev.abhishek.ecommerce.modules.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "brand", target = "brand")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "inventory", target = "inventory")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "seller.id", target = "seller_id")
    @Mapping(source = "category.id", target = "category_id")
    @Mapping(source = "images", target = "imageList")
    ProductDto toDto(Product product);

    List<ProductDto> toDtoList(List<Product> products);

    @Mapping(source = "request.name", target = "name")
    @Mapping(source = "request.brand", target = "brand")
    @Mapping(source = "request.price", target = "price")
    @Mapping(source = "request.inventory", target = "inventory")
    @Mapping(source = "request.description", target = "description")
    @Mapping(source = "category", target = "category")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(CreateProductRequest request, Category category);

    @Mapping(source = "request.name", target = "name")
    @Mapping(source = "request.brand", target = "brand")
    @Mapping(source = "request.price", target = "price")
    @Mapping(source = "request.inventory", target = "inventory")
    @Mapping(source = "request.description", target = "description")
    @Mapping(source = "category", target = "category")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UpdateProductRequest request,
                                 @MappingTarget Product product,
                                 Category category);

    default List<ImageDto> map(List<Image> images) {
        return ImageMapper.toDtoList(images);
    }
}
