package de.ftracker.services.repositories;

import de.ftracker.domain.model.pots.UndistributedPotAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PotSummaryRepository extends JpaRepository<UndistributedPotAmount, Long> {
    @Query(value = """
    select associated_expenses_id
    from undistributed_pot_amount_associated_expenses
    where undistributed_pot_amount_user_id = :userId
    """, nativeQuery = true)
    List<Long> findAssociatedExpenseIdsRaw(@Param("userId") Long userId);

    public Optional<UndistributedPotAmount> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UndistributedPotAmount s WHERE s.user.id=:userId")
    void deleteByUserId(@Param("userId") Long userId);
}
