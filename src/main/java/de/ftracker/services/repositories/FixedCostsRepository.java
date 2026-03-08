package de.ftracker.services.repositories;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.cost.Cost;
import de.ftracker.domain.model.cost.FixedCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FixedCostsRepository extends JpaRepository<FixedCost, Long> {

    List<FixedCost> findByUser(AppUser user);

    @Modifying
    @Transactional
    @Query("DELETE FROM FixedCost f WHERE f.descr = :descr AND f.startYear = :year AND f.startMonth = :month")
    void deleteByDescrAndStart(@Param("descr") String descr,
                               @Param("year") int year,
                               @Param("month") int month);
}
