package com.arishi.AXAM.repo;

import com.arishi.AXAM.enums.CategoryStatus;
import com.arishi.AXAM.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByTitleIgnoreCaseAndDeletedAtIsNull(String title);

    Optional<Category> findByTitleIgnoreCaseAndDeletedAtIsNull(String title);
    Optional<Category>findByIdAndDeletedAtIsNull(Long id);

    List<Category> findByStatusAndDeletedAtIsNull(CategoryStatus status);;
}
