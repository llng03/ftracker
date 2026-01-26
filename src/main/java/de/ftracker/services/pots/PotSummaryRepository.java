package de.ftracker.services.pots;

import de.ftracker.domain.model.potsDTOs.UndistributedPotAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PotSummaryRepository extends JpaRepository<UndistributedPotAmount, Long> {
}
