package de.ftracker.services;

import de.ftracker.domain.model.costDTOs.Cost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Repository
public interface CostRepository extends JpaRepository<Cost, Long> {
}
