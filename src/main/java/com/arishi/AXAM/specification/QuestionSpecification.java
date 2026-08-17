package com.arishi.AXAM.specification;

import com.arishi.AXAM.enums.DifficultyLevel;
import com.arishi.AXAM.model.Question;
import org.springframework.data.jpa.domain.Specification;

public class QuestionSpecification {

    public static Specification<Question> isNotDeleted() {

        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Question> hasCategory(String category) {

        return (root, query, cb) -> {

            if (category == null || category.isBlank()) {
                return null;
            }

            return cb.equal(cb.lower(root.get("category").get("title")), category.toLowerCase());
        };
    }

    public static Specification<Question> hasDifficultyLevel(DifficultyLevel difficultyLevel) {

        return (root, query, cb) -> {

            if (difficultyLevel == null) {
                return null;
            }

            return cb.equal(root.get("difficultyLevel"), difficultyLevel);
        };
    }


}