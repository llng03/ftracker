package de.ftracker.services;

import de.ftracker.domain.model.CostTables;
import de.ftracker.domain.model.costDTOs.Cost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.Optional;

//funktioniert das ganze jetzt wirklich schon? hab ich JPA verstanden?
// Jetzt noch fixedCostRepo implementieren und dann vllt mal YT video zu JPA anschauen - kann ja nicht sein
@Repository
public interface CostTablesRepository extends JpaRepository<CostTables, Long> {
    Optional<CostTables> findByMonthAndYear(int month, int year);

    @Query("select c from CostTables c where c.year = :year and c.month = :month")
    Optional<CostTables> customFind(@Param("year") int year, @Param("month") int month);
}