package de.ftracker.services;

import de.ftracker.domain.model.costDTOs.Cost;
import de.ftracker.domain.model.costDTOs.FixedCost;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class MonthOverviewDTO {
    private int currYear;
    private int currMonth;

    // - - costs - - //
    private List<Cost> monthsIncome;
    private List<Cost> monthsExpense;

    private List<FixedCost> monthsFixedIncome;
    private List<FixedCost> monthsFixedExpense;

    private List<FixedCost> fixedIncome;
    private List<FixedCost> fixedExpense;

    private List<Cost> allMonthsIncome;
    private List<Cost> allMonthsExpense;

    // - - sums - - //
    private BigDecimal sumIn;
    private BigDecimal sumOut;
    private BigDecimal difference;

}
