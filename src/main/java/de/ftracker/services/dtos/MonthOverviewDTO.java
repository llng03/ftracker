package de.ftracker.services.dtos;

import de.ftracker.domain.model.cost.Cost;
import de.ftracker.domain.model.cost.FixedCost;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class MonthOverviewDTO {
    private int currYear;
    private int currMonth;

    private List<String> allCategories;

    // - - costs - - //
    private List<Cost> monthsIncome;
    private List<Cost> monthsExpense;

    private List<Cost> monthsFixedIncome;
    private List<Cost> monthsFixedExpense;

    // - - sums - - //
    private BigDecimal sumIn;
    private BigDecimal sumOut;
    private BigDecimal difference;

}
