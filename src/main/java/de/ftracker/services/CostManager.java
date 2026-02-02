package de.ftracker.services;

import de.ftracker.domain.model.CostTables;
import de.ftracker.domain.model.costDTOs.Cost;
import de.ftracker.domain.model.costDTOs.FixedCost;
import de.ftracker.domain.model.costDTOs.FixedCostForm;
import de.ftracker.domain.model.costDTOs.Interval;
import de.ftracker.domain.services.CostAggregationService;
import de.ftracker.services.pots.PotManager;
import de.ftracker.utils.MonthlySums;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CostManager {
    private final CostTablesRepository costTablesRepository;
    private final FixedCostsRepository fixedCostsRepository;
    private final CostAggregationService costAggregationService;


    public CostManager(CostTablesRepository costTablesRepository, FixedCostsRepository fixedCostsRepository) {
        this.costTablesRepository = costTablesRepository;
        this.fixedCostsRepository = fixedCostsRepository;
        this.costAggregationService = new CostAggregationService();
    }
    /*
    getIncome: the whole income table
    getFixedIncome: the whole fixed income table
    getAllIncome: whole income and fixed income table combined
    getMonthsIncome: every income which date is in the given month
    getMonthsFixedIncome: every fixedIncome, that timespan contains the given Month
    getAllMonthsIncome: getMonthsIncome and getMonthsFixedIncome combined
     */

    // -- READ --
    public CostTables getTablesOf(YearMonth yearMonth) {
        return costTablesRepository.findByMonthAndYear(yearMonth.getMonthValue(), yearMonth.getYear())
                .orElseGet(() -> {
                    CostTables newTables = new CostTables();
                    newTables.setYearMonth(yearMonth);
                    return costTablesRepository.save(newTables);
                });
    }

    public List<Cost> getMonthsIncome(YearMonth yearMonth) {
        return getTablesOf(yearMonth).getIncomes();
    }

    public List<Cost> getMonthsIncome(int year, int month) {
        return getMonthsIncome(YearMonth.of(year, month));
    }

    public List<Cost> getMonthsExp(YearMonth yearMonth) {
        return getTablesOf(yearMonth).getExpenses();
    }

    public List<Cost> getMonthsExp(int year, int month) {
        return getMonthsExp(YearMonth.of(year, month));
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

    public List<FixedCost> getMonthsFixedIncome(YearMonth yearMonth) {
        return costAggregationService.getApplicableFixedCosts(getFixedIncome(), yearMonth);
    }

    public List<FixedCost> getMonthsFixedIncome(int year, int month) {
        return getMonthsFixedIncome(YearMonth.of(year, month));
    }

    public List<FixedCost> getMonthsFixedExp(YearMonth yearMonth) {
        return costAggregationService.getApplicableFixedCosts(getFixedExp(), yearMonth);
    }

    public List<FixedCost> getMonthsFixedExp(int year, int month) {
        return getMonthsFixedExp(YearMonth.of(year, month));
    }


    public List<Cost> getAllMonthsIncome(YearMonth month) {
        List<Cost> income = getMonthsIncome(month);
        income.addAll(costAggregationService.getApplicableFixedCosts(getFixedIncome(), month));
        return income;
    }

    public List<Cost> getAllMonthsIncome(int year, int month) {
        return getAllMonthsIncome(YearMonth.of(year, month));
    }

    public List<Cost> getAllMonthsExp(YearMonth month) {
        List<Cost> exp = getMonthsExp(month);
        exp.addAll(costAggregationService.getApplicableFixedCosts(getFixedExp(), month));
        return exp;
    }

    public List<Cost> getAllMonthsExp(int year, int month) {
        return getAllMonthsExp(YearMonth.of(year, month));
    }


    //DAS HIER IST EIGENTLICH NUR WEITERLEITUNG DER METHODEN
    public MonthlySums calculateThisMonthsSums(YearMonth month) {
        return costAggregationService.calculateMonthlySums(
                getAllMonthsIncome(month),
                getAllMonthsExp(month)
        );
    }

    public MonthlySums calculateThisMonthsSums(int year, int month) {
        return calculateThisMonthsSums(YearMonth.of(year, month));
    }

    public BigDecimal getMonthlyCost(FixedCostForm costForm) {
        return costAggregationService.getMonthlyAmount(costForm);
    }

    public BigDecimal getMonthlyCost(FixedCost fixedCost) {
        return costAggregationService.getMonthlyAmount(fixedCost);
    }

    // - - WRITE - -
    @Transactional
    public void addMonthsIncome(int year, int month, Cost income) {
        CostTables costTables = costTablesRepository.findByMonthAndYear(month, year)
                .orElseThrow();
        costTables.addCostToIncomes(income);
    }

    @Transactional
    public void addMonthsExp(int year, int month, Cost exp) {
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
        if(exp.getFrequency() == Interval.MONTHLY) {
            fixedCostsRepository.save(exp);
        } else {
            fixedCostsRepository.save(
                    new FixedCost(exp.getDescr(), costAggregationService.getMonthlyAmount(exp), false, Interval.MONTHLY, exp.getStart(), exp.getEndValue())
            );
        }
    }

    // - - DELETE - -
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

    // - - POTS - -
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
