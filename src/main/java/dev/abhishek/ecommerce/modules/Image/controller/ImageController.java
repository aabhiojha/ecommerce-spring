package dev.abhishek.ecommerce.modules.Image.controller;

import dev.abhishek.ecommerce.modules.Image.dtos.UploadImageDto;
import dev.abhishek.ecommerce.modules.Image.service.ImageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final ImageServiceImpl imageServiceImpl;

    @PostMapping(value = "/upload")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<String> uploadProductImage(
            @RequestPart("file") MultipartFile file,
            @RequestParam("productId") Long productId
    ) throws Exception {

        UploadImageDto uploadImageDto = new UploadImageDto();
        uploadImageDto.setFile(file);
        uploadImageDto.setProductId(productId);

        // need to set the metadata in the image table as well
        // and put the image object in minio
        imageServiceImpl.uploadImageToProduct(uploadImageDto);

        return ResponseEntity.ok("Uploaded successfully");
    }
}
