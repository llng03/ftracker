package de.ftracker.services.repositories;

import de.ftracker.domain.model.pots.BudgetPot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PotRepository extends JpaRepository<BudgetPot, Long> {

    Optional<BudgetPot> findByName(String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM BudgetPot p WHERE p.user.id=:userId")
    void deleteByUserId(@Param("userId") Long userId);
}
