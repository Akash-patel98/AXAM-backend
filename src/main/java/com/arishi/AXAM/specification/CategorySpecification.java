package com.arishi.AXAM.specification;

import com.arishi.AXAM.enums.CategoryStatus;
import com.arishi.AXAM.model.Category;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification {

    public static Specification<Category> titleContains(String title) {

        return (root, query, criteriaBuilder) -> {

            if (title == null || title.isBlank()) return null;

            return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Category> hasStatus(CategoryStatus status) {

        return (root, query, criteriaBuilder) -> {

            if (status == null) return null;

            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<Category> isNotDeleted() {

        return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("deletedAt"));
    }
}