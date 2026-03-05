package de.ftracker.services.dtos;

import de.ftracker.domain.model.cost.FixedCost;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FixedCostOverviewDTO {
    private List<FixedCost> currentFixedIncome;
    private List<FixedCost> currentFixedExpense;

    private List<FixedCost> futureFixedIncome;
    private List<FixedCost> futureFixedExpense;

    private List<FixedCost> pastFixedIncome;
    private List<FixedCost> pastFixedExpense;
}
