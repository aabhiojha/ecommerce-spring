package dev.abhishek.ecommerce.modules.product.dto;

import dev.abhishek.ecommerce.modules.image.dtos.ImageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto implements Serializable {

    private Long id;

    private String name;

    private String brand;

    private BigDecimal price;

    private Long inventory;

    private String description;

    private Long seller_id;

    private Long category_id;

    private List<ImageDto> imageList;

    private Float rating;

    private Integer reviewCount;
}
