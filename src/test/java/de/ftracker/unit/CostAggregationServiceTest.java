package de.ftracker.unit;

import de.ftracker.domain.model.costDTOs.Cost;
import de.ftracker.domain.model.costDTOs.FixedCost;
import de.ftracker.domain.model.costDTOs.Interval;
import de.ftracker.domain.services.CostAggregationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import static java.time.Month.AUGUST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class CostAggregationServiceTest {

    private CostAggregationService costAggregationService;

    @BeforeEach
    public void setUp() {
        costAggregationService = new CostAggregationService();
    }

    // -- getApplicableFixedCost
    @Test
    @DisplayName("getApplicableFixedCosts funktioniert für aktuellen Monat")
    void test1() {
        FixedCost fixedCost = new FixedCost("fixedIncome",
                new BigDecimal("50"), true, Interval.MONTHLY, YearMonth.of(2025, Month.JULY), null);
        List<FixedCost> fixedCosts = List.of(fixedCost);

        List<FixedCost> applFixedCosts = costAggregationService.getApplicableFixedCosts(fixedCosts, YearMonth.of(2025, Month.JULY));

        assertThat(applFixedCosts).anyMatch(fc -> fc.getDescr().equals("fixedIncome"));

    }
    @Test
    @DisplayName("gAFC funktioniert für nächsten Monat")
    void test2() {
        FixedCost fixedCost = new FixedCost("fixedIncome",
                new BigDecimal("50"), true, Interval.MONTHLY, YearMonth.of(2025, Month.JULY), null);

        List<FixedCost> fixedCosts = List.of(fixedCost);

        List<FixedCost> applFixedCosts = costAggregationService.getApplicableFixedCosts(fixedCosts, YearMonth.of(2025, Month.AUGUST));

        assertThat(applFixedCosts).anyMatch(fc -> fc.getDescr().equals("fixedIncome"));

    }

    @Test
    @DisplayName("gAFC funktioniert für letzen Monat nicht")
    void test3() {
        FixedCost fixedCost = new FixedCost("fixedIncome",
                new BigDecimal("50"), true, Interval.MONTHLY, YearMonth.of(2025, Month.JULY), null);
        List<FixedCost> fixedCosts = List.of(fixedCost);

        List<FixedCost> applFixedCosts = costAggregationService.getApplicableFixedCosts(fixedCosts, YearMonth.of(2025, Month.JUNE));

        assertThat(applFixedCosts).noneMatch(fc -> fc.getDescr().equals("fixedIncome"));

    }
    @Test
    @DisplayName("gAFC funktioniert für nächsten Monat nicht falls unerwünscht")
    void test4() {
        FixedCost fixedCost = new FixedCost("fixedIncome",
                new BigDecimal("50"), true, Interval.MONTHLY, YearMonth.of(2025, Month.JULY), YearMonth.of(2025, AUGUST));
        List<FixedCost> fixedCosts = List.of(fixedCost);

        List<FixedCost> applFixedCosts = costAggregationService.getApplicableFixedCosts(fixedCosts, YearMonth.of(2025, Month.SEPTEMBER));

        assertThat(applFixedCosts).noneMatch(fc -> fc.getDescr().equals("fixedIncome"));

    }

    @Test
    @DisplayName("gAFC funktioniert für letzten erwünschten Monat noch")
    void test5() {
        FixedCost fixedCost = new FixedCost("fixedIncome",
                new BigDecimal("50"), true, Interval.MONTHLY, YearMonth.of(2025, Month.JULY), YearMonth.of(2025, AUGUST));
        List<FixedCost> fixedCosts = List.of(fixedCost);

        List<FixedCost> applFixedCosts = costAggregationService.getApplicableFixedCosts(fixedCosts, YearMonth.of(2025, Month.AUGUST));

        assertThat(applFixedCosts).anyMatch(fc -> fc.getDescr().equals("fixedIncome"));

    }
}
