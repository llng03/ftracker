package de.ftracker.services.repositories;

import de.ftracker.domain.model.cost.Cost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CostRepository extends JpaRepository<Cost, Long> {
}
