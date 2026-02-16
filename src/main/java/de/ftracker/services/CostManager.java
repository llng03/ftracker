package de.ftracker.services;

import de.ftracker.domain.model.CostTables;
import de.ftracker.domain.model.costDTOs.*;
import de.ftracker.domain.model.potsDTOs.BudgetPot;
import de.ftracker.domain.model.potsDTOs.PotEntry;
import de.ftracker.domain.model.potsDTOs.PotForRegularExp;
import de.ftracker.domain.services.CostAggregationService;
import de.ftracker.services.DTOs.DeleteEntryRequest;
import de.ftracker.services.DTOs.UpdateCostRequest;
import de.ftracker.services.DTOs.UpdateFixedCostRequest;
import de.ftracker.utils.MonthNavigation;
import de.ftracker.utils.MonthlySums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CostManager {
    private final CostRepository costRepository;
    private final CostTablesRepository costTablesRepository;
    private final FixedCostsRepository fixedCostsRepository;
    private final CostAggregationService costAggregationService;
    private final CategoryRepository categoryRepository;
    private final PotManager potManager;

    @Autowired
    public CostManager(CostTablesRepository costTablesRepository,
                       FixedCostsRepository fixedCostsRepository,
                       CostRepository costRepository, PotSummaryRepository potSummaryRepository,
                       PotRepository potRepository, CategoryRepository categoryRepository,
                       PotManager potManager) {
        this.costTablesRepository = costTablesRepository;
        this.fixedCostsRepository = fixedCostsRepository;
        this.costAggregationService = new CostAggregationService();
        this.costRepository = costRepository;
        this.categoryRepository = categoryRepository;
        this.potManager = potManager;
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
                .filter(Cost::getIsIncome)
                .collect(Collectors.toList());
    }

    public List<FixedCost> getFixedExp() {
        return fixedCostsRepository.findAll().stream()
                .filter(c -> !c.getIsIncome())
                .collect(Collectors.toList());
    }

    public List<Cost> getMonthsFixedIncome(YearMonth yearMonth) {
        return costAggregationService.getApplicableFixedIncome(getFixedIncome(), yearMonth);
    }

    public List<Cost> getMonthsFixedIncome(int year, int month) {
        return getMonthsFixedIncome(YearMonth.of(year, month));
    }

    public List<Cost> getMonthsFixedExp(YearMonth yearMonth) {
        return costAggregationService.getApplicableFixedExp(getFixedExp(), yearMonth);
    }

    public List<Cost> getMonthsFixedExp(int year, int month) {
        return getMonthsFixedExp(YearMonth.of(year, month));
    }


    public List<Cost> getAllMonthsIncome(YearMonth month) {
        List<Cost> income = getMonthsIncome(month);
        income.addAll(costAggregationService.getApplicableFixedExp(getFixedIncome(), month));
        return income;
    }

    public List<Cost> getAllMonthsIncome(int year, int month) {
        return getAllMonthsIncome(YearMonth.of(year, month));
    }

    public List<Cost> getAllMonthsExp(YearMonth month) {
        List<Cost> exp = getMonthsExp(month);
        exp.addAll(costAggregationService.getApplicableFixedExp(getFixedExp(), month));
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
        fixedCostsRepository.save(exp);

        if(exp.getFrequency() != Interval.MONTHLY) {
            CostAggregationService costAggregationService = new CostAggregationService();
            MonthNavigation monthNavigation = new MonthNavigation(exp.getStart());
            YearMonth lastMonth = monthNavigation.getPrevYearMonth();
            potManager.addPot(new PotForRegularExp(
                    exp.getDescr(),
                    lastMonth,
                    lastMonth,
                    costAggregationService.getMonthlyAmount(exp),
                    exp.getId(),
                    exp.getFrequency())
            );
        }
        /*if(exp.getFrequency() == Interval.MONTHLY) {
            fixedCostsRepository.save(exp);
        } else {
            fixedCostsRepository.save(
                new FixedCost(exp.getDescr(),
                        costAggregationService.getMonthlyAmount(exp),
                        false,
                        Interval.MONTHLY,
                        exp.getStart(),
                        exp.getEndValue()
                )
            );
        }*/
    }

    // - - DELETE - -
    public void deleteFromFixedCosts(Long id) {
        potManager.decouplePots(id);
        fixedCostsRepository.deleteById(id);
    }

    @Transactional
    public void deleteFromCosts(Long id, int year, int month, PotManager potManager) {
        System.out.println("DELETE requested id=" + id);

        CostTables table = costTablesRepository.customFind(year, month)
                .orElseThrow(() -> new IllegalArgumentException(
                "No CostTable found for " + year + "-" + month
        ));

        System.out.println("Income");
        table.getIncomes().forEach(c -> System.out.println(c.getId() + " " + c.getDescr() + " class=" + c.getClass()));
        System.out.println("Expenses");
        table.getExpenses().forEach(c -> System.out.println(c.getId() + " " + c.getDescr()));

        boolean incomePresent = table.getIncomes().stream().anyMatch(e -> e.getId().equals(id));
        boolean expensePresent = table.getExpenses().stream().anyMatch(e -> e.getId().equals(id));

        System.out.println("incomePresent: " + incomePresent + " expensePresent: " + expensePresent);

        Cost cost = table.getIncomes().stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElseGet(() -> table.getExpenses().stream()
                        .filter(e -> e.getId().equals(id))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Found no Cost with id " + id)));

        potManager.deletePotEntryWithCostId(cost);
        potManager.getExpenseIdsRaw().forEach(System.out::println);

        table.deleteCostById(cost.getId());

        costTablesRepository.save(table);

        costRepository.deleteById(cost.getId());

    }

    // - - POTS - -
    @Transactional
    public void addToPots(CostTables thisTables, PotManager potManager, BigDecimal amount) {
        thisTables.addCostToExpenses("auf Pots zu Verteilen", amount);
        potManager.addToUndistributed(amount);
        costTablesRepository.save(thisTables);
    }

    @Transactional
    public void addToPot(CostTables thisTables, PotManager potManager, BigDecimal amount, String potName) {
        thisTables.addCostToExpenses("auf Pot " + potName + " verteilen", amount);
        potManager.addToUndistributed(amount);
        potManager.distribute(amount, potName);
        costTablesRepository.save(thisTables);
    }

    @Transactional
    public void addToPot(int year, int month, PotManager potManager, BigDecimal amount, Long potId) {
        CostTables tables = costTablesRepository.findByMonthAndYear(month, year).orElseThrow( () ->
                new IllegalArgumentException("Keine Daten für " + month + "-" + year + " gefunden.")
        );
        Optional<BudgetPot> pot = potManager.getPotById(potId);
        if(pot.isPresent()) {
            BudgetPot actualPot = pot.get();
            Cost cost = new Cost("in Pot " + actualPot.getName() + " gelegt", amount, false);
            potManager.addEntry(actualPot, LocalDate.now(), amount, cost);
            costRepository.save(cost);
            tables.addCostToExpenses(cost);
            costTablesRepository.save(tables);
            potManager.saveInRepo(actualPot);
        } else {
            Cost cost = new Cost("auf Pots zu Verteilen", amount, false);
            tables.addCostToExpenses(cost);
            costRepository.saveAndFlush(cost);
            costTablesRepository.saveAndFlush(tables);
            potManager.addToUndistributed(amount);
            potManager.addCostToUndistributed(cost);
        }
    }

    public void updateCost(UpdateCostRequest updateCostRequest, int year, int month, PotManager potManager) {
        CostTables tables = costTablesRepository.findByMonthAndYear(month,year).orElseThrow( () ->
                new IllegalArgumentException("Found no tables for " +  year + "-" + month));
        Cost cost = tables.findCostById(updateCostRequest.getCostId()).orElseThrow(() ->
                new IllegalArgumentException("Found no cost with id " + updateCostRequest.getCostId()));
        cost.setDescr(updateCostRequest.getDescr());
        cost.setAmount(updateCostRequest.getAmount());
        cost.setCategory(updateCostRequest.getCategory());
        potManager.updateAssociatedPotEntry(updateCostRequest.getCostId(), updateCostRequest.getAmount());
        costTablesRepository.save(tables);
    }

    public void updateFixedCost(UpdateFixedCostRequest updateFixedCostRequest) {
        FixedCost fCost = fixedCostsRepository.findById(updateFixedCostRequest.getCostId())
                .orElseThrow( () -> new IllegalArgumentException(
                        "Found no FixedCost with id "+ updateFixedCostRequest.getCostId()
                )
        );
        fCost.setDescr(updateFixedCostRequest.getDescr());
        fCost.setAmount(updateFixedCostRequest.getAmount());
        fCost.setFrequency(updateFixedCostRequest.getFrequency());
        fCost.setStartMonth(updateFixedCostRequest.getStart().getMonth().getValue());
        fCost.setStartYear(updateFixedCostRequest.getStart().getYear());
        if(updateFixedCostRequest.getEnd() == null) {
            fCost.setEndMonth(null);
            fCost.setEndYear(null);
        } else {
            fCost.setEndMonth(updateFixedCostRequest.getEnd().getMonth().getValue());
            fCost.setEndYear(updateFixedCostRequest.getEnd().getYear());
        }


        fixedCostsRepository.save(fCost);
    }
    @Transactional
    public void deletePotEntry(DeleteEntryRequest deleteEntryRequest, PotManager potManager) {
        BudgetPot pot = potManager.findPotById(deleteEntryRequest.getPotId());
        PotEntry entry = pot.getEntryById(deleteEntryRequest.getEntryId());

        deleteAssociatedCostIfPresent(potManager, entry);
        potManager.deleteEntry(pot, entry);
    }

    public void deleteAssociatedCostIfPresent(PotManager potManager, PotEntry entry) {
        Cost cost = entry.getCost();
        if(cost == null) {
            return;
        }

        CostTables tables = costTablesRepository.findByMonthAndYear(
                entry.getDate().getMonthValue(), entry.getDate().getYear())
                .orElseThrow( () -> new IllegalArgumentException(
                        "No Tables found from " + entry.getDate().getYear() + "-" + entry.getDate().getMonthValue()));
        tables.deleteCostById(cost.getId());
        costTablesRepository.save(tables);
    }

    public void changeFixedCost(UpdateFixedCostRequest updateFixedCostRequest, YearMonth changeMonth) {
        //set endmonth of old fixedcost of month before changeMonth
        FixedCost oldFixedCost = fixedCostsRepository.findById(updateFixedCostRequest.getCostId())
                .orElseThrow(() -> new IllegalArgumentException("did not find id"));
        UpdateFixedCostRequest updateOldFixedCostRequest = createRequestForEndingFixedCost(
                oldFixedCost, changeMonth);
        updateFixedCost(updateOldFixedCostRequest);

        //create new fixed costs with new data and start month before changeMonth
        FixedCost newFixedCost = new FixedCost(
              updateFixedCostRequest.getDescr(),
              updateFixedCostRequest.getAmount(),
              oldFixedCost.getIsIncome(),
              updateFixedCostRequest.getFrequency(),
              changeMonth,
              updateFixedCostRequest.getEnd()
        );
        if(newFixedCost.getIsIncome()) {
            addToFixedIncome(newFixedCost);
        } else {
            addToFixedExp(newFixedCost);
        }
        fixedCostsRepository.save(newFixedCost);
    }

    private UpdateFixedCostRequest createRequestForEndingFixedCost(
            FixedCost oldFixedCost,
            YearMonth changeMonth) {
        UpdateFixedCostRequest updateOldFixedCostRequest = new UpdateFixedCostRequest();
        updateOldFixedCostRequest.setCostId(oldFixedCost.getId());
        updateOldFixedCostRequest.setDescr(oldFixedCost.getDescr());
        updateOldFixedCostRequest.setAmount(oldFixedCost.getAmount());
        updateOldFixedCostRequest.setFrequency(oldFixedCost.getFrequency());
        updateOldFixedCostRequest.setStart(oldFixedCost.getStart());

        updateOldFixedCostRequest.setEnd(
                new MonthNavigation(changeMonth).getPrevYearMonth()
        );
        return updateOldFixedCostRequest;
    }

    public List<Long> getFCostsIdsWithNonMonthlyRegExp() {
        return getFixedExp()
                .stream()
                .filter(fCost -> fCost.getFrequency() != Interval.MONTHLY)
                .map(Cost::getId)
                .toList();
    }
    
    public List<String> getAllCategories() {
        return categoryRepository.findAll().stream().map(c -> c.getCategoryName()).toList();
    }
    
    public void addCategory(String name) {
        Category newCategory = new Category(name);
        categoryRepository.save(newCategory);
    }

    public Category getOrCreate(String name) {
        return categoryRepository.findByCategoryName("DEFAULT")
                .orElseGet(() -> categoryRepository.save(new Category(name)));
    }
}
