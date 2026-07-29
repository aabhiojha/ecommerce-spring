package dev.abhishek.ecommerce.modules.image.controller;

import dev.abhishek.ecommerce.modules.image.dtos.ImageDto;
import dev.abhishek.ecommerce.modules.image.dtos.UploadImageDto;
import dev.abhishek.ecommerce.modules.image.dtos.UploadMultipleImagesDto;
import dev.abhishek.ecommerce.modules.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Tag(name = "Images", description = "Endpoints for managing product images")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Upload product image", description = "Uploads a single image for a specific product")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageDto> uploadProductImage(
            @RequestPart("file") MultipartFile file,
            @RequestParam("productId") Long productId
    ) {
        UploadImageDto uploadImageDto = new UploadImageDto();
        uploadImageDto.setFile(file);
        uploadImageDto.setProductId(productId);

        return new ResponseEntity<>(imageService.uploadImageToProduct(uploadImageDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Upload multiple product images", description = "Uploads multiple images for a specific product")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ImageDto>> uploadProductImages(
            @RequestPart("files") MultipartFile[] files,
            @RequestParam("productId") Long productId
    ) {
        UploadMultipleImagesDto uploadMultipleImagesDto = new UploadMultipleImagesDto();
        uploadMultipleImagesDto.setProductId(productId);
        uploadMultipleImagesDto.setFiles(Arrays.asList(files));

        return new ResponseEntity<>(imageService.uploadImagesToProduct(uploadMultipleImagesDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Get product images", description = "Retrieves all images associated with a specific product")
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ImageDto>> getProductImages(@PathVariable Long productId) {
        return ResponseEntity.ok(imageService.getImagesOfProduct(productId));
    }

    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Delete image", description = "Deletes a specific product image by its ID")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        imageService.deleteImage(imageId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
