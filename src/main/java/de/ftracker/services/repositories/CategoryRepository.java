package de.ftracker.services.repositories;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.cost.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCategoryName(String name);
    List<Category> findCategoriesByUser(AppUser user);
    Optional<Category> findByUserAndCategoryName(AppUser user, String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM Category c WHERE c.user.id=:userId")
    void deleteByUser(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Category c WHERE c.user.id=:userId AND c.categoryName=:categoryName")
    void deleteByUserAndCategoryName(Long userId, String categoryName);
}
