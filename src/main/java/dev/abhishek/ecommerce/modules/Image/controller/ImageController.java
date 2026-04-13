package dev.abhishek.ecommerce.modules.Image.controller;

import dev.abhishek.ecommerce.modules.Image.dtos.ImageDto;
import dev.abhishek.ecommerce.modules.Image.dtos.UploadImageDto;
import dev.abhishek.ecommerce.modules.Image.dtos.UploadMultipleImagesDto;
import dev.abhishek.ecommerce.modules.Image.entity.Image;
import dev.abhishek.ecommerce.modules.Image.mapper.ImageMapper;
import dev.abhishek.ecommerce.modules.Image.repository.ImageRepository;
import dev.abhishek.ecommerce.modules.Image.service.ImageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final ImageServiceImpl imageServiceImpl;
    private final ImageRepository imageRepository;

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadProductImage(
            @RequestPart("file") MultipartFile file,
            @RequestParam("productId") Long productId
    ) throws Exception {

        UploadImageDto uploadImageDto = new UploadImageDto();
        uploadImageDto.setFile(file);
        uploadImageDto.setProductId(productId);

        // put the image object in s3
        // need to set the metadata in the image table as well
        imageServiceImpl.uploadImageToProduct(uploadImageDto);

        return ResponseEntity.ok("Uploaded successfully");
    }

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping(value = "/upload-multiple")
    public ResponseEntity<List<ImageDto>> uploadProductImages(
            @RequestPart("files") MultipartFile[] files,
            @RequestParam("productId") Long productId
    ) throws Exception {

        UploadMultipleImagesDto uploadMultipleImagesDto = new UploadMultipleImagesDto();
        uploadMultipleImagesDto.setProductId(productId);
        uploadMultipleImagesDto.setFiles(Arrays.asList(files));

        return ResponseEntity.ok(imageServiceImpl.uploadImagesToProduct(uploadMultipleImagesDto));
    }

    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<ImageDto>> getAllImages() {
        List<Image> all = imageRepository.findAll();
        List<ImageDto> dtoList = ImageMapper.toDtoList(all);
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }
}
