package de.ftracker.services;

import de.ftracker.model.CostTables;
import de.ftracker.model.costDTOs.Cost;
import de.ftracker.model.costDTOs.FixedCost;
import de.ftracker.model.costDTOs.FixedCostForm;
import de.ftracker.model.costDTOs.Interval;
import de.ftracker.services.pots.PotManager;
import de.ftracker.utils.IntervalCount;
import de.ftracker.utils.MonthlySums;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CostManager {
    private final CostTablesRepository costTablesRepository;
    private final FixedCostsRepository fixedCostsRepository;


    public CostManager(CostTablesRepository costTablesRepository, FixedCostsRepository fixedCostsRepository) {
        this.costTablesRepository = costTablesRepository;
        this.fixedCostsRepository = fixedCostsRepository;
    }

    public CostTables getTablesOf(YearMonth yearMonth) {
        return costTablesRepository.findByMonthAndYear(yearMonth.getMonthValue(), yearMonth.getYear())
                .orElseGet(() -> {
                    CostTables newTables = new CostTables();
                    newTables.setYearMonth(yearMonth);
                    return costTablesRepository.save(newTables);
                });
    }

    public List<FixedCost> getFixedIncome() {
        return fixedCostsRepository.findAll().stream()
                .filter(c -> c.getIsIncome())
                .collect(Collectors.toList());
    }

    public List<FixedCost> getFixedExp() {
        return fixedCostsRepository.findAll().stream()
                .filter(c -> !c.getIsIncome())
                .collect(Collectors.toList());
    }

    public List<Cost> getIncome(YearMonth yearMonth) {
        return getTablesOf(yearMonth).getIncomes();
    }

    public List<Cost> getExp(YearMonth yearMonth) {
        return getTablesOf(yearMonth).getExpenses();
    }

    public List<Cost> getAllMonthsIncome(YearMonth month) {
        List<Cost> income = getIncome(month);
        income.addAll(getMonthsIncome(month));
        return income;
    }

    public List<Cost> getAllMonthsExp(YearMonth month) {
        List<Cost> exp = getExp(month);
        exp.addAll(getMonthsExp(month));
        return exp;
    }

    public List<Cost> getMonthsExp(YearMonth month) {
        return getFixedExp().stream()
                .filter(fc -> !fc.getStart().isAfter(month))
                .filter(fc -> fc.getEnd().map(end -> !end.isBefore(month)).orElse(true))
                .collect(Collectors.toList());
    }

    public List<Cost> getMonthsIncome(YearMonth month) {
        return getFixedIncome().stream()
                .filter(fc -> !fc.getStart().isAfter(month))
                .filter(fc -> fc.getEnd().map(end -> !end.isBefore(month)).orElse(true))
                .collect(Collectors.toList());
    }

    public List<Cost> getApplicableFixedCosts(YearMonth month) {
        List<Cost> incomesAndExpensesM = getMonthsIncome(month);
        incomesAndExpensesM.addAll(getMonthsExp(month));
        return incomesAndExpensesM;
    }

    public static BigDecimal getMonthlyCost(FixedCostForm costForm) {
        return costForm.getAmount().divide(BigDecimal.valueOf(IntervalCount.countMonths(costForm.getFrequency())), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal getMonthlyCost(FixedCost expense) {
        return expense.getAmount().divide(BigDecimal.valueOf(IntervalCount.countMonths(expense.getFrequency())), 2, RoundingMode.HALF_UP);
    }

    @Transactional
    public void addIncome(int year, int month, Cost income) {
        CostTables costTables = costTablesRepository.findByMonthAndYear(month, year)
                .orElseThrow();
        costTables.addCostToIncomes(income);
    }

    @Transactional
    public void addExp(int year, int month, Cost exp) {
        CostTables costTables = costTablesRepository.findByMonthAndYear(month, year)
                .orElseThrow();
        costTables.addCostToExpenses(exp);
    }

    @Transactional
    public void addToFixedIncome(FixedCostForm incomeForm) {
        FixedCost fixedCost = new FixedCost();
        fixedCost.setDescr(incomeForm.getDescr());
        fixedCost.setAmount(incomeForm.getAmount());
        fixedCost.setIsIncome(incomeForm.getIsIncome());
        fixedCost.setFrequency(incomeForm.getFrequency());
        fixedCost.setStart(incomeForm.getStart());
        fixedCost.setEnd(incomeForm.getEnd());
        addToFixedIncome(fixedCost);
    }

    @Transactional
    public void addToFixedIncome(FixedCost income) {
        System.out.println("now we save: " + income + "into fixedIncomeRepository");
        fixedCostsRepository.save(income);
    }

    @Transactional
    public void addToFixedExp(FixedCostForm expForm) {
        FixedCost fixedCost = new FixedCost();
        fixedCost.setDescr(expForm.getDescr());
        fixedCost.setAmount(expForm.getAmount());
        fixedCost.setIsIncome(false);
        fixedCost.setFrequency(expForm.getFrequency());
        fixedCost.setStart(expForm.getStart());
        fixedCost.setEnd(expForm.getEnd());
        addToFixedExp(fixedCost);
    }

    public void addToFixedExp(FixedCost exp) {
        System.out.println("now we save: " + exp + "into fixedExpRepository");
        if(exp.getFrequency() == Interval.MONTHLY) {
            fixedCostsRepository.save(exp);
        } else {
            fixedCostsRepository.save(
                    new FixedCost(exp.getDescr(), getMonthlyCost(exp), false, Interval.MONTHLY, exp.getStart(), exp.getEndValue())
            );
        }
    }

    public void deleteFromFixedIncome(FixedCost income) {
        fixedCostsRepository.delete(income);
    }



    public void deleteFromFixedIncome(String income, YearMonth start) {
        fixedCostsRepository.deleteByDescrAndStart(income, start.getYear(), start.getMonthValue());
    }

    public void deleteFromFixedExp(FixedCost expense) {
        fixedCostsRepository.delete(expense);
    }

    public void deleteFromFixedExp(String expense, YearMonth start) {
        fixedCostsRepository.deleteByDescrAndStart(expense, start.getYear(), start.getMonthValue());
    }

    public void deleteFromIncome(Long id, int year, int month) {
        CostTables table = costTablesRepository.customFind(year, month)
                .orElseThrow(() -> new IllegalArgumentException(
                "No CostTable found for " + year + "-" + month
        ));
        table.getIncomes().removeIf(e -> e.getId().equals(id));
        costTablesRepository.save(table);
    }

    public void deleteFromExp(Long id, int year, int month) {
        CostTables table = costTablesRepository.customFind(year, month)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No CostTable found for " + year + "-" + month
                ));
        table.getExpenses().removeIf(e -> e.getId().equals(id));
        costTablesRepository.save(table);
    }

    public BigDecimal getThisMonthsIncomeSum(YearMonth month) {
        List<Cost> incomes = getMonthsIncome(month);
        incomes.addAll(getTablesOf(month).getIncomes());
        return incomes.stream()
                .map(Cost::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getThisMonthsExpSum(YearMonth month) {
        List<Cost> expenses = getMonthsExp(month);
        expenses.addAll(getTablesOf(month).getExpenses());
        return expenses.stream()
                .map(Cost::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public MonthlySums calculateThisMonthsSums(YearMonth month) {
        BigDecimal sumIn = getThisMonthsIncomeSum(month);
        BigDecimal sumOut = getThisMonthsExpSum(month);
        return new MonthlySums(sumIn, sumOut);
    }

    public void addToPots(CostTables thisTables, PotManager potManager, BigDecimal amount) {
        thisTables.addCostToExpenses("auf Pots zu Verteilen", amount);
        potManager.addToUndistributed(amount);
        costTablesRepository.save(thisTables);
    }

    public void addToPot(CostTables thisTables, PotManager potManager, BigDecimal amount, String potName) {
        thisTables.addCostToExpenses("auf Pot " + potName + " verteilen", amount);
        potManager.addToUndistributed(amount);
        potManager.distribute(amount, potName);
        costTablesRepository.save(thisTables);
    }
}
