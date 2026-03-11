package de.ftracker.services.repositories;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.cost.Category;
import de.ftracker.domain.model.cost.Cost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CostRepository extends JpaRepository<Cost, Long> {
    @Query()
    List<Cost> findByUserAndCategory(AppUser user, Category deletedCategory);
}
