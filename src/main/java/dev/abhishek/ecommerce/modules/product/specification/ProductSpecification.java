package dev.abhishek.ecommerce.modules.product.specification;

import dev.abhishek.ecommerce.modules.product.entity.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductSpecification {
    public static Specification<Product> getSpecification(Long id, String name, String description) {
        return new Specification<Product>() {
            @Override
            public @Nullable Predicate toPredicate(Root<Product> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                if (name != null && !name.isBlank()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")),
                            normalizeText(name)
                    ));
                }

                if (id != null) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
                }

                if (description != null && !description.isBlank()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("description")),
                            normalizeText(description)
                    ));
                }

                if (predicates.isEmpty()) {
                    return criteriaBuilder.conjunction();
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }

            private String normalizeText(String text) {
                return "%" + text.trim().toLowerCase(Locale.ROOT) + "%";
            }
        };
    }
}
