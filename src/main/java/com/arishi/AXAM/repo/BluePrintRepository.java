package com.arishi.AXAM.repo;

import com.arishi.AXAM.model.BluePrint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BluePrintRepository extends JpaRepository<BluePrint, Long> {

    boolean existsByTitleIgnoreCaseAndDeletedAtIsNull(String title);

    List<BluePrint> findByDeletedAtIsNull();

    Optional<BluePrint> findByTitleIgnoreCaseAndDeletedAtIsNull(String title);
}
