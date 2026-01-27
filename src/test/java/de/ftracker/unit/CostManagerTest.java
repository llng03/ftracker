package de.ftracker.unit;

import de.ftracker.services.*;
import de.ftracker.domain.model.costDTOs.Cost;
import de.ftracker.domain.model.costDTOs.FixedCost;
import de.ftracker.domain.model.costDTOs.Interval;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import static java.time.Month.AUGUST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CostManagerTest {
    @Mock
    private CostTablesRepository costTablesRepository;

    @Mock
    private FixedCostsRepository fixedCostsRepository;

    @InjectMocks
    private CostManager costManager;

    // - - getMonthlyCost - - //
    @Test
    @DisplayName("getMonthlyCost correct for QUATERLY")
    void test6() {
        FixedCost quaterInc = new FixedCost("quaterInc",new BigDecimal("75"), false,
                Interval.QUARTERLY, YearMonth.of(2025, AUGUST), null);
        assertThat(costManager.getMonthlyCost(quaterInc)).isEqualByComparingTo(new BigDecimal("25"));
    }

    @Test
    @DisplayName("getMonthlyCost correct for SEMI_ANNUAL")
    void test7() {
        FixedCost quaterInc = new FixedCost("quaterInc",new BigDecimal("60"), false,
                Interval.SEMI_ANNUAL, YearMonth.of(2025, AUGUST), null);
        assertThat(costManager.getMonthlyCost(quaterInc)).isEqualByComparingTo(new BigDecimal("10"));
    }

    @Test
    @DisplayName("getMonthlyCost correct for ANNUAL")
    void test8() {
        FixedCost quaterInc = new FixedCost("quaterInc",new BigDecimal("120"), false,
                Interval.ANNUAL, YearMonth.of(2025, AUGUST), null);
        assertThat(costManager.getMonthlyCost(quaterInc)).isEqualByComparingTo(new BigDecimal("10"));
    }
}
