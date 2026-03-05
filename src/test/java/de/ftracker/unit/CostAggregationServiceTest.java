package de.ftracker.unit;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.cost.Category;
import de.ftracker.domain.model.cost.Cost;
import de.ftracker.domain.model.cost.FixedCost;
import de.ftracker.domain.model.cost.Interval;
import de.ftracker.domain.services.CostAggregationService;
import de.ftracker.domain.services.CostService;
import de.ftracker.services.CostManager;
import de.ftracker.utils.MonthlySums;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static de.ftracker.domain.model.cost.Interval.MONTHLY;
import static java.time.Month.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class CostAggregationServiceTest {

    @Mock
    private CostService costService;

    @InjectMocks
    private CostAggregationService costAggregationService;

    private List<FixedCost> fixedCosts;
    private FixedCost startsBeforeNoEnd;
    private FixedCost startsInMonthEndsInMonth;
    private FixedCost startsBeforeEndsAfter;
    private FixedCost startsAfter;
    private FixedCost endedBefore;
    private YearMonth month;

    @BeforeEach
    public void setUp() {
        costAggregationService = new CostAggregationService(costService);
        month = YearMonth.of(2026, 3);

        startsBeforeNoEnd = new FixedCost("A", BigDecimal.ONE, true,
                new Category(), new AppUser(), MONTHLY, YearMonth.of(2026, 1), null);

        startsInMonthEndsInMonth = new FixedCost("B", BigDecimal.ONE, true,
                new Category(), new AppUser(), MONTHLY, YearMonth.of(2026, 3), YearMonth.of(2026, 3));

        startsBeforeEndsAfter = new FixedCost("C", BigDecimal.ONE, true,
                new Category(), new AppUser(), MONTHLY, YearMonth.of(2026, 1),YearMonth.of(2026, 4));

        startsAfter = new FixedCost("D", BigDecimal.ONE, true,
                new Category(), new AppUser(), MONTHLY, YearMonth.of(2026, 4), YearMonth.of(2026, 6));

        endedBefore = new FixedCost("E", BigDecimal.ONE, true,
                new Category(), new AppUser(), MONTHLY, YearMonth.of(2026, 1), YearMonth.of(2026, 2));

        fixedCosts = List.of(startsBeforeNoEnd, startsInMonthEndsInMonth, startsBeforeEndsAfter, startsAfter, endedBefore);
    }

    //-- getPresentFixedCost
    @Test
    @DisplayName("getPresentFixedCosts works for current month")
    void test1() {
        List<FixedCost> result = costAggregationService.getPresentFixedCosts(fixedCosts, month);

        assertThat(result).contains(startsBeforeNoEnd, startsInMonthEndsInMonth, startsBeforeEndsAfter);
        assertThat(result).doesNotContain(startsAfter, endedBefore);
    }

    @Test
    @DisplayName("getPastFixedCost works for current month")
    void test2() {
        List<FixedCost> result = costAggregationService.getPastFixedCosts(fixedCosts, month);

        assertThat(result).containsExactly(endedBefore);
    }

    @Test
    @DisplayName("getFutureFixedCost works for currentMonth")
    void test3() {
        List<FixedCost> result = costAggregationService.getFutureFixedCosts(fixedCosts, month);

        assertThat(result).containsExactly(startsAfter);
    }

    // -- sum
    @Test
    @DisplayName("calculateMonthlySums sums income and expenses")
    void calculateMonthlySums_sumsIncomeAndExpenses() {
        Cost income1 = new Cost("income1", BigDecimal.valueOf(100.10), true, new AppUser(), new Category());
        Cost income2 = new Cost("income2", BigDecimal.valueOf(9.90), true, new AppUser(), new Category());
        Cost exp1 = new Cost("exp1", BigDecimal.valueOf(10.00), true, new AppUser(), new Category());
        Cost exp2 = new Cost("exp2", BigDecimal.valueOf(2.50), true, new AppUser(), new Category());

        MonthlySums sums = costAggregationService.calculateMonthlySums(List.of(income1, income2), List.of(exp1, exp2));

        assertThat(sums.sumIn).isEqualByComparingTo(new BigDecimal("110.00"));
        assertThat(sums.sumOut).isEqualByComparingTo(new BigDecimal("12.50"));
    }

     //-- getApplicableFixedCost
}
