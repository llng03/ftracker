package de.ftracker.domain.services;

import de.ftracker.domain.model.CostTables;
import de.ftracker.domain.model.costDTOs.Cost;
import de.ftracker.domain.model.costDTOs.FixedCost;
import de.ftracker.domain.model.costDTOs.FixedCostForm;
import de.ftracker.utils.IntervalCount;
import de.ftracker.utils.MonthlySums;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CostAggregationService {
    public List<FixedCost> getApplicableFixedCosts(List<FixedCost> fixedCosts, YearMonth month) {
        return fixedCosts.stream()
                .filter(fc -> appliesTo(fc, month))
                .collect(Collectors.toList());
    }

    public MonthlySums calculateMonthlySums(List<Cost> income, List<Cost> exp) {
        BigDecimal incomeSum = sum(income);
        BigDecimal expSum = sum(exp);
        return new MonthlySums(incomeSum, expSum);
    }

    public BigDecimal getMonthlyAmount(FixedCost cost) {
        return cost.getAmount().divide(
                BigDecimal.valueOf(IntervalCount.countMonths(cost.getFrequency())),
                2,
                RoundingMode.HALF_UP
        );
    }

    public BigDecimal getMonthlyAmount(FixedCostForm fixedCostForm) {
        return fixedCostForm.getAmount().divide(
                BigDecimal.valueOf(IntervalCount.countMonths(fixedCostForm.getFrequency())),
                2,
                RoundingMode.HALF_UP
        );
    }

    private BigDecimal sum(List<Cost> costs) {
        return costs.stream()
                .map(Cost::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean appliesTo(FixedCost cost, YearMonth month) {
        return !cost.getStart().isAfter(month)
                && cost.getEnd().map(end -> !end.isBefore(month)).orElse(true);
    }
}
