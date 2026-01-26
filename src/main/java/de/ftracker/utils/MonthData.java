package de.ftracker.utils;

import de.ftracker.domain.model.costDTOs.Cost;
import de.ftracker.domain.model.costDTOs.FixedCost;

import java.util.List;

public class MonthData {
    public final List<Cost> income;
    public final List<Cost> expense;
    public final List<FixedCost> fixedIncome;
    public final List<FixedCost> fixedExpense;

    public MonthData(List<Cost> income, List<Cost> expense, List<FixedCost> fixedIncome, List<FixedCost> fixedExpense) {
        this.income = income;
        this.expense = expense;
        this.fixedIncome = fixedIncome;
        this.fixedExpense = fixedExpense;
    }



}
