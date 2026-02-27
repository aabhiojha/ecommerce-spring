package dev.abhishek.ecommerce.modules.Image.controller;

import dev.abhishek.ecommerce.modules.Image.dtos.UploadImageDto;
import dev.abhishek.ecommerce.modules.Image.service.ImageService;
import dev.abhishek.ecommerce.modules.Image.service.MinioService;
import io.minio.messages.Upload;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final MinioService minioService;

//    @PostMapping("/{productId}/images")
//    public ResponseEntity<?> uploadImage(
//            @PathVariable Long productId,
//            @RequestParam("file") MultipartFile file
//    ) throws IOException {
//
//        imageService.uploadImage(productId, file);
//        return ResponseEntity.ok().build();
//    }

    @PostMapping(value = "/upload"
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("productId") Long productId
    ) throws Exception {

        UploadImageDto uploadImageDto = new UploadImageDto();
        uploadImageDto.setFile(file);
        uploadImageDto.setProductId(productId);

        // need to set the metadata in the image table as well
        // and put the image object in minio
        imageService.uploadImage(uploadImageDto);

        return ResponseEntity.ok("Uploaded successfully");
    }
}
