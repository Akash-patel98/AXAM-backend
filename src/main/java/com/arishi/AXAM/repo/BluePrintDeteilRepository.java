package com.arishi.AXAM.repo;

import com.arishi.AXAM.model.BluePrintDeteil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BluePrintDeteilRepository extends JpaRepository<BluePrintDeteil, Long> {
    List<BluePrintDeteil> findByBluePrintIdAndDeletedAtIsNull(long id);
}
