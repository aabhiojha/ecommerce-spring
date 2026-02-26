package dev.abhishek.ecommerce.modules.Image.controller;

import dev.abhishek.ecommerce.modules.Image.service.ImageService;
import dev.abhishek.ecommerce.modules.Image.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws Exception {

        minioService.uploadFile(
                file.getOriginalFilename(),
                file.getInputStream(),
                file.getContentType()
        );

        return ResponseEntity.ok("Uploaded successfully");
    }
}
