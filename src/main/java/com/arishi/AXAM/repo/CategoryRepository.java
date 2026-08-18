package com.arishi.AXAM.repo;

import com.arishi.AXAM.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    boolean existsByTitleIgnoreCaseAndDeletedAtIsNull(String title);

    Optional<Category> findByTitleIgnoreCaseAndDeletedAtIsNull(String title);

    Optional<Category> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByTitleIgnoreCaseAndDeletedAtIsNullAndIdNot(String title, Long id);

}
