package de.ftracker.services;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.CostTables;
import de.ftracker.domain.model.cost.*;
import de.ftracker.domain.model.pots.BudgetPot;
import de.ftracker.domain.model.pots.PotEntry;
import de.ftracker.domain.model.pots.PotForRegularExp;
import de.ftracker.domain.services.CostAggregationService;
import de.ftracker.domain.services.FixedCostService;
import de.ftracker.services.dtos.CostDTO;
import de.ftracker.services.dtos.DeleteEntryRequest;
import de.ftracker.services.dtos.UpdateCostRequest;
import de.ftracker.services.dtos.UpdateFixedCostRequest;
import de.ftracker.services.repositories.CategoryRepository;
import de.ftracker.services.repositories.CostRepository;
import de.ftracker.services.repositories.CostTablesRepository;
import de.ftracker.services.repositories.FixedCostsRepository;
import de.ftracker.utils.MonthNavigation;
import de.ftracker.utils.MonthlySums;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CostManager {
    private final CostRepository costRepository;
    private final CostTablesRepository costTablesRepository;
    private final FixedCostsRepository fixedCostsRepository;
    private final CostAggregationService costAggregationService;
    private final CategoryRepository categoryRepository;
    private final PotManager potManager;
    private final FixedCostService fixedCostService;

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

    public List<FixedCost> getFixedCosts(AppUser user) {
        return fixedCostsRepository.findByUser(user);
    }

    public List<Cost> getMonthsIncome(CostTables monthsTables, Long userId) {
        return monthsTables.getIncomes(userId);
    }

    public List<Cost> getMonthsIncome(YearMonth yearMonth, Long userId) {
        return getMonthsIncome(getTablesOf(yearMonth), userId);
    }

    public List<Cost> getMonthsIncome(int year, int month, Long userId) {
        return getMonthsIncome(YearMonth.of(year, month), userId);
    }

    public List<Cost> getMonthsExp(CostTables monthsTables, Long userId) {
        return monthsTables.getExpenses(userId);
    }

    public List<Cost> getMonthsExp(YearMonth yearMonth, Long userId) {
        return getMonthsExp(getTablesOf(yearMonth), userId);
    }

    public List<Cost> getMonthsExp(int year, int month, Long userId) {
        return getMonthsExp(YearMonth.of(year, month), userId);
    }

    public List<FixedCost> getFixedIncome(AppUser user) {
        return getFixedIncome(getFixedCosts(user));
    }

    public List<FixedCost> getFixedIncome(List<FixedCost> fixedCosts) {
        return fixedCosts.stream()
                .filter(Cost::getIsIncome)
                .collect(Collectors.toList());
    }

    public List<FixedCost> getFixedExp(AppUser user) {
        return getFixedExp(getFixedCosts(user));
    }

    public List<FixedCost> getFixedExp(List<FixedCost> fixedCosts) {
        return fixedCosts.stream()
                .filter(c -> !c.getIsIncome())
                .collect(Collectors.toList());
    }

    public List<Cost> getMonthsFixedIncome(List<FixedCost> fixedIncome, YearMonth yearMonth) {
        return costAggregationService.getApplicableFixedIncome(fixedIncome, yearMonth);
    }

    public List<Cost> getMonthsFixedIncome(AppUser user, YearMonth yearMonth) {
        return getMonthsFixedIncome(getFixedCosts(user), yearMonth);
    }

    public List<Cost> getMonthsFixedIncome(AppUser user, int year, int month) {
        return getMonthsFixedIncome(user, YearMonth.of(year, month));
    }

    public List<Cost> getMonthsFixedExp(List<FixedCost> fixedExp, YearMonth yearMonth) {
        return costAggregationService.getApplicableFixedExp(fixedExp, yearMonth);
    }

    public List<Cost> getMonthsFixedExp(AppUser user, YearMonth yearMonth) {
        return getMonthsFixedExp(getFixedCosts(user), yearMonth);
    }

    public List<Cost> getMonthsFixedExp(AppUser user, int year, int month) {
        return getMonthsFixedExp(user, YearMonth.of(year, month));
    }

    public List<Cost> getAllMonthsIncome(List<FixedCost> fixedIncome, List<Cost> monthsIncome, YearMonth month) {
        List<Cost> allMonthsIncome = new ArrayList<>();
        allMonthsIncome.addAll(monthsIncome);
        allMonthsIncome.addAll(costAggregationService.getApplicableFixedIncome(fixedIncome, month));
        return allMonthsIncome;
    }

    public List<Cost> getAllMonthsIncome(YearMonth month, AppUser user) {
        return getAllMonthsIncome(getFixedIncome(user), getMonthsIncome(month, user.getId()), month);
    }

    public List<Cost> getAllMonthsIncome(int year, int month, AppUser user) {
        return getAllMonthsIncome(YearMonth.of(year, month), user);
    }

    public List<Cost> getAllMonthsExp(List<FixedCost> fixedExp, List<Cost> monthsExp, YearMonth month) {
        List<Cost> allMonthsExp = new ArrayList<>();
        allMonthsExp.addAll(monthsExp);
        allMonthsExp.addAll(costAggregationService.getApplicableFixedExp(fixedExp, month));
        return allMonthsExp;
    }

    public List<Cost> getAllMonthsExp(YearMonth month, AppUser user) {
        return getAllMonthsExp(getFixedExp(user), getMonthsExp(month, user.getId()), month);
    }

    public List<Cost> getAllMonthsExp(int year, int month, AppUser user) {
        return getAllMonthsExp(YearMonth.of(year, month), user);
    }


    public MonthlySums calculatethisMonthsSums(List<Cost> allMonthsIncome, List<Cost> allMonthsExp) {
        return costAggregationService.calculateMonthlySums(allMonthsIncome, allMonthsExp);
    }
    public MonthlySums calculateThisMonthsSums(YearMonth month, AppUser user) {
        return calculatethisMonthsSums(getAllMonthsIncome(month, user), getAllMonthsExp(month, user));
    }

    public MonthlySums calculateThisMonthsSums(int year, int month, AppUser user) {
        return calculateThisMonthsSums(YearMonth.of(year, month), user);
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
    public void addToFixedIncome(FixedCostForm incomeForm, AppUser user) {
        FixedCost fixedCost = new FixedCost();
        fixedCost.setDescr(incomeForm.getDescr());
        fixedCost.setAmount(incomeForm.getAmount());
        fixedCost.setIsIncome(incomeForm.getIsIncome());
        fixedCost.setUser(user);
        fixedCost.setCategory(categoryRepository.findByUserAndCategoryName(user, "default").orElseThrow());
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
    public void addToFixedExp(FixedCostForm expForm, AppUser user) {
        FixedCost fixedCost = new FixedCost();
        fixedCost.setDescr(expForm.getDescr());
        fixedCost.setAmount(expForm.getAmount());
        fixedCost.setIsIncome(false);
        fixedCost.setUser(user);
        fixedCost.setFrequency(expForm.getFrequency());
        fixedCost.setCategory(categoryRepository.findByUserAndCategoryName(user, "default").orElseThrow());
        fixedCost.setStart(expForm.getStart());
        fixedCost.setEnd(expForm.getEnd());
        addToFixedExp(fixedCost, user);
    }

    public void addToFixedExp(FixedCost exp, AppUser user) {
        fixedCostsRepository.save(exp);

        if(exp.getFrequency() != Interval.MONTHLY) {
            MonthNavigation monthNavigation = new MonthNavigation(exp.getStart());
            YearMonth lastMonth = monthNavigation.getPrevYearMonth();
            potManager.addPot(new PotForRegularExp(
                    exp.getDescr(),
                    user,
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
    public void deleteFromFixedCosts(Long id, AppUser user) {
        potManager.decouplePots(id, user);
        fixedCostsRepository.deleteById(id);
    }

    @Transactional
    public void deleteFromCosts(Long id, AppUser user, int year, int month, PotManager potManager) {
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

        potManager.deletePotEntryWithCostId(cost, user);
        potManager.getExpenseIdsRaw(user).forEach(System.out::println);

        table.deleteCostById(cost.getId());

        costTablesRepository.save(table);

        costRepository.deleteById(cost.getId());

    }

    // - - POTS - -
    @Transactional
    public void addToPots(CostTables thisTables, AppUser user, PotManager potManager, BigDecimal amount) {
        Category toPotsCategory = categoryRepository.findByUserAndCategoryName(user, "-> pots").orElseThrow(() ->
                new IllegalArgumentException("no -> pots-category found for user: " + user.getName()));
        thisTables.addCostToExpenses("auf Pots zu Verteilen", amount, toPotsCategory, user);
        potManager.addToUndistributed(amount, user);
        costTablesRepository.save(thisTables);
    }

    @Transactional
    public void addToPot(CostTables thisTables, AppUser user,  PotManager potManager, BigDecimal amount, String potName) {
        Category toPotsCategory = categoryRepository.findByUserAndCategoryName(user, "-> pots").orElseThrow(() ->
                new IllegalArgumentException("no -> pots-category found for user: " + user.getName()));
        thisTables.addCostToExpenses("auf Pot " + potName + " verteilen", amount, toPotsCategory, user);
        potManager.addToUndistributed(amount, user);
        potManager.distribute(amount, potName, user);
        costTablesRepository.save(thisTables);
    }

    @Transactional
    public void addToPot(int year, int month, AppUser user, PotManager potManager, BigDecimal amount, Long potId) {
        CostTables tables = costTablesRepository.findByMonthAndYear(month, year).orElseThrow( () ->
                new IllegalArgumentException("Keine Daten für " + month + "-" + year + " gefunden.")
        );
        boolean potPresent = !(potId == null);
        if(potPresent) {
            potPresent = potManager.getPotById(potId).isPresent();
        }
        Category toPotsCategory = categoryRepository.findByUserAndCategoryName(user, "-> pots").orElseThrow(() ->
                new IllegalArgumentException("Did not find -> pots-Category"));
        if(potPresent) {
            BudgetPot actualPot = potManager.getPotById(potId).get();
            Cost cost = new Cost("in Pot " + actualPot.getName() + " gelegt", amount, false, user, toPotsCategory);
            potManager.addEntry(actualPot, LocalDate.now(), amount, cost);
            costRepository.save(cost);
            tables.addCostToExpenses(cost);
            costTablesRepository.save(tables);
            potManager.saveInRepo(actualPot);
        } else {
            Cost cost = new Cost("auf Pots zu Verteilen", amount, false, user, toPotsCategory);
            tables.addCostToExpenses(cost);
            costRepository.saveAndFlush(cost);
            costTablesRepository.saveAndFlush(tables);
            potManager.addToUndistributed(amount, user);
            potManager.addCostToUndistributed(cost, user);
        }
    }

    public void updateCost(UpdateCostRequest updateCostRequest, AppUser user, int year, int month, PotManager potManager) {
        CostTables tables = costTablesRepository.findByMonthAndYear(month,year).orElseThrow( () ->
                new IllegalArgumentException("Found no tables for " +  year + "-" + month));
        Cost cost = tables.findCostById(updateCostRequest.getCostId()).orElseThrow(() ->
                new IllegalArgumentException("Found no cost with id " + updateCostRequest.getCostId()));
        cost.setDescr(updateCostRequest.getDescr());
        cost.setAmount(updateCostRequest.getAmount());
        cost.setUser(user);
        cost.setCategory(categoryRepository.findByUserAndCategoryName(user, updateCostRequest.getCategory()).orElseThrow());
        potManager.updateAssociatedPotEntry(updateCostRequest.getCostId(), updateCostRequest.getAmount());
        costTablesRepository.save(tables);
    }

    public void updateFixedCost(UpdateFixedCostRequest updateFixedCostRequest, AppUser user) {
        FixedCost fCost = fixedCostsRepository.findById(updateFixedCostRequest.getCostId())
                .orElseThrow( () -> new IllegalArgumentException(
                        "Found no FixedCost with id "+ updateFixedCostRequest.getCostId()
                )
        );
        fCost.setDescr(updateFixedCostRequest.getDescr());
        fCost.setAmount(updateFixedCostRequest.getAmount());
        fCost.setUser(user);
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

    @Transactional
    public void changeFixedCost(AppUser user, UpdateFixedCostRequest updateFixedCostRequest, YearMonth changeMonth) {
        //set endmonth of old fixedcost of month before changeMonth
        FixedCost oldFixedCost = fixedCostsRepository.findById(updateFixedCostRequest.getCostId())
                .orElseThrow(() -> new IllegalArgumentException("did not find id"));
        UpdateFixedCostRequest updateOldFixedCostRequest = createRequestForEndingFixedCost(
                oldFixedCost, changeMonth);
        updateFixedCost(updateOldFixedCostRequest, user);

        //create new fixed costs with new data and start month before changeMonth
        FixedCost newFixedCost = fixedCostService.create(
              updateFixedCostRequest.getDescr(),
              updateFixedCostRequest.getAmount(),
              oldFixedCost.getIsIncome(),
              user,
              updateFixedCostRequest.getFrequency(),
              changeMonth,
              updateFixedCostRequest.getEnd()
        );
        if(newFixedCost.getIsIncome()) {
            addToFixedIncome(newFixedCost);
        } else {
            addToFixedExp(newFixedCost, user);
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

    public List<Long> getFCostsIdsWithNonMonthlyRegExp(AppUser user) {
        return getFixedExp(user)
                .stream()
                .filter(fCost -> fCost.getFrequency() != Interval.MONTHLY)
                .map(Cost::getId)
                .toList();
    }
    
    public List<String> getAllCategories(AppUser user) {
        return categoryRepository.findCategoriesByUser(user).stream()
                .map(Category::getCategoryName)
                .sorted(String::compareTo)
                .toList();
    }
    
    public void addCategory(String name, AppUser user) {
        Category newCategory = new Category(name, user);
        categoryRepository.save(newCategory);
    }

    private Cost createCostFromCostDTO(CostDTO costDTO, AppUser user) {
        System.out.println("ISINCOME old: " + costDTO.isIncome());
        Cost cost = new Cost();
        cost.setDescr(costDTO.getDescr());
        cost.setAmount(costDTO.getAmount());
        cost.setIncome(costDTO.isIncome());
        cost.setUser(user);
        cost.setCategory(categoryRepository.findByUserAndCategoryName(user, costDTO.getCategory()).orElseThrow());

        costRepository.save(cost);

        System.out.println("ISINCOME:" + cost.isIncome());
        return cost;
    }

    @Transactional
    public void addCost(Cost cost, int year, int month) {
        if(cost.getIsIncome()) {
            addMonthsIncome(year, month, cost);
        } else {
            addMonthsExp(year, month, cost);
        }
    }

    @Transactional
    public void addCost(CostDTO costDTO, AppUser user, int year, int month) {
        addCost(createCostFromCostDTO(costDTO, user), year, month);
    }

    @Transactional
    public void addFixedCost(@Valid FixedCostForm fixedCost, AppUser user) {
        if(fixedCost.getIsIncome()) {
            addToFixedIncome(fixedCost, user);
        } else {
            addToFixedExp(fixedCost, user);
        }
    }

    @Transactional
    public void deleteCategory(String categoryName, AppUser user) {
        Category deletedCategory = categoryRepository
                .findByUserAndCategoryName(user, categoryName).orElseThrow(() ->
                        new IllegalArgumentException("Did not find Category to delete."));
        List<Cost> costsToModify = costRepository.findByUserAndCategory(user, deletedCategory);
        costsToModify.forEach(cost -> System.out.println(cost.getDescr()));
        Category defaultCategory = categoryRepository.findByUserAndCategoryName(user, "default")
                .orElseThrow(() -> new IllegalArgumentException("Did not find default category."));

        for(Cost cost: costsToModify) {
            cost.setCategory(defaultCategory);
            System.out.println("set " + cost.getDescr() + " to category " + defaultCategory.getCategoryName());
            costRepository.saveAndFlush(cost);
        }

        categoryRepository.deleteByUserAndCategoryName(user.getId(), categoryName);
    }
}
