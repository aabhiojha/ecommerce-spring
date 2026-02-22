package dev.abhishek.ecommerce.modules.Image.repository;

import dev.abhishek.ecommerce.modules.Image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
