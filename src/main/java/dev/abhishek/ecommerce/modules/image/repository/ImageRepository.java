package dev.abhishek.ecommerce.modules.image.repository;

import dev.abhishek.ecommerce.modules.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByProduct_Id(Long productId);
}
