package de.ftracker.services.repositories;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.cost.Category;
import de.ftracker.domain.model.cost.Cost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CostRepository extends JpaRepository<Cost, Long> {
    @Query()
    List<Cost> findByUserAndCategory(AppUser user, Category deletedCategory);

    List<Cost> findByUser(AppUser user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Cost c WHERE c.user.id=:userId")
    void deleteByUser(@Param("userId") Long userId);
}
