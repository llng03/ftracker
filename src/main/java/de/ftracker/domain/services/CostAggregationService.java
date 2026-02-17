package de.ftracker.domain.services;

import de.ftracker.domain.model.costDTOs.Cost;
import de.ftracker.domain.model.costDTOs.FixedCost;
import de.ftracker.domain.model.costDTOs.FixedCostForm;
import de.ftracker.domain.model.costDTOs.Interval;
import de.ftracker.utils.IntervalCount;
import de.ftracker.utils.MonthNavigation;
import de.ftracker.utils.MonthlySums;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

public class CostAggregationService {
    public List<Cost> getApplicableFixedExp(List<FixedCost> fixedCosts, YearMonth month) {
         return getPresentFixedCosts(fixedCosts, month)
                 .stream()
                 .map( fCost -> {
                    if(fCost.getFrequency() != Interval.MONTHLY) {
                        return new Cost(
                                fCost.getId(),
                                fCost.getDescr(),
                                getMonthlyAmount(fCost),
                                fCost.getIsIncome()
                        );
                    } else {
                        return fCost;
                    }
                }
                ).toList();
    }
    public List<FixedCost> getPresentFixedCosts(List<FixedCost> fixedCosts, YearMonth month) {
        return fixedCosts.stream()
                .filter(fc -> appliesTo(fc, month))
                .collect(Collectors.toList());
    }

    public List<FixedCost> getPastFixedCosts(List<FixedCost> fixedCosts, YearMonth month) {
        return fixedCosts.stream()
                .filter(fc -> fc.getEnd().isPresent())
                .filter(fc -> fc.getEnd().get().isBefore(month))
                .collect(Collectors.toList());
    }

    public List<FixedCost> getFutureFixedCosts(List<FixedCost> fixedCosts, YearMonth month) {
        return fixedCosts.stream()
                .filter(fc -> fc.getStart().isAfter(month))
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
                && ((cost.getEnd().isEmpty()) || (!cost.getEnd().get().isBefore(month)));
    }

    public List<Cost> getApplicableFixedIncome(List<FixedCost> fixedIncome, YearMonth yearMonth) {
        return getPresentFixedCosts(fixedIncome, yearMonth)
                .stream()
                .filter(fCost -> isApplicable(fCost, yearMonth))
                .map(fCost -> new Cost(fCost.getId(), fCost.getDescr(), fCost.getAmount(), fCost.getIsIncome()))
                .toList();
    }

    private boolean isApplicable(FixedCost fCost, YearMonth yearMonth) {
        int numOfMonthsPast = MonthNavigation.computeNumberOfMonthsPast(fCost.getStart(), yearMonth);
        return switch (fCost.getFrequency()) {
            case ANNUAL -> numOfMonthsPast % 12 == 0;
            case SEMI_ANNUAL -> numOfMonthsPast % 6 == 0;
            case QUARTERLY -> numOfMonthsPast % 3 == 0;
            default -> true;
        };


    }
}
