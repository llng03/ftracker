/*package de.ftracker.db;

import de.ftracker.model.CostTables;
import de.ftracker.model.costDTOs.Cost;
import de.ftracker.services.CostTablesRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class CostTablesRepositoryTest {
    @Autowired
    CostTablesRepository costTablesRepository;

    @Test
    @DisplayName("save and find by id works")
    void test1() {
        CostTables income = new CostTables(YearMonth.of(2025, Month.JUNE));
        costTablesRepository.save(income);

        Optional<CostTables> loaded = costTablesRepository.findById(income.getId());

        assertThat(loaded).isPresent();
        assertThat(loaded.get().getYearMonth()).isEqualTo(YearMonth.of(2025, Month.JUNE));
    }

}*/
