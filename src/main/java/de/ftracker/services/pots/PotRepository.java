package de.ftracker.services.pots;

import de.ftracker.model.potsDTOs.BudgetPot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PotRepository extends JpaRepository<BudgetPot, Long> {

    Optional<BudgetPot> findByName(String name);
}
