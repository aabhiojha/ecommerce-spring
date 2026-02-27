package dev.abhishek.ecommerce.modules.Image.mapper;

import dev.abhishek.ecommerce.modules.Image.dtos.ImageDto;
import dev.abhishek.ecommerce.modules.Image.dtos.UploadImageDto;
import dev.abhishek.ecommerce.modules.Image.entity.Image;
import dev.abhishek.ecommerce.modules.product.entity.Product;

import java.util.List;

public final class ImageMapper {
    public static ImageDto toDto(Image image) {
        if (image == null) return null;

        return ImageDto.builder()
                .id(image.getId())
                .fileName(image.getFileName())
                .fileType(image.getFileType())
                .downloadUrl(image.getDownloadUrl())
                .productId(image.getProduct().getId())
                .build();
    }

    public static List<ImageDto> toDtoList(List<Image> images) {
        if (images == null) return List.of();

        return images.stream()
                .map(ImageMapper::toDto)
                .toList();
    }

    public static Image toEntity(UploadImageDto request, Product product, String imgDownloadUrl){
        if (request == null) return null;

        Image image = new Image();

        image.setFileName(request.getFile().getOriginalFilename());
        image.setFileType(request.getFile().getContentType());
        image.setDownloadUrl(imgDownloadUrl);
        image.setProduct(product);

        return image;
    }

//    public static

}
