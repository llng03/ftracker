package de.ftracker.services;

import de.ftracker.domain.model.costDTOs.FixedCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface FixedCostsRepository extends JpaRepository<FixedCost, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM FixedCost f WHERE f.descr = :descr AND f.startYear = :year AND f.startMonth = :month")
    void deleteByDescrAndStart(@Param("descr") String descr,
                               @Param("year") int year,
                               @Param("month") int month);
}
