package de.ftracker.services;

import de.ftracker.domain.model.potsDTOs.UndistributedPotAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PotSummaryRepository extends JpaRepository<UndistributedPotAmount, Long> {
    @Query(value = """
    select associated_expenses_id
    from undistributed_pot_amount_associated_expenses
    where undistributed_pot_amount_id = 1
    """, nativeQuery = true)
    List<Long> findAssociatedExpenseIdsRaw();
}
