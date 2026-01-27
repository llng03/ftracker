package de.ftracker.controller;

import de.ftracker.services.CostManager;
import de.ftracker.domain.model.CostTables;
import de.ftracker.domain.model.costDTOs.*;
import de.ftracker.domain.model.potsDTOs.PotForRegularExp;
import de.ftracker.services.pots.PotManager;
import de.ftracker.utils.MonthData;
import de.ftracker.utils.MonthNavigation;
import de.ftracker.utils.MonthlySums;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

@Controller
public class WebController {
    private final CostManager costManager;
    private final PotManager potManager;

    @Autowired
    public WebController(CostManager costManager, PotManager potManager) {
        this.costManager = costManager;
        this.potManager = potManager;
    }

    @GetMapping("/")
    public String index(Model model) {
        return indexMonth(model, YearMonth.now().getYear(), YearMonth.now().getMonthValue());
    }

    @GetMapping("/{currYear}/{currMonth}")
    public String indexMonth(Model model, @PathVariable int currYear, @PathVariable int currMonth) {
        YearMonth thisYearMonth = YearMonth.of(currYear, currMonth);
        prepareModel(model, thisYearMonth);
        return "index";
    }

    @PostMapping("/{currYear}/{currMonth}/income")
    public String postIncome(Model model, @ModelAttribute @Valid Cost income, BindingResult bindingResult,
                             @PathVariable int currYear, @PathVariable int currMonth) {
        if(bindingResult.hasErrors()){
            model.addAttribute("income", income);

            prepareModel(model, YearMonth.of(currYear, currMonth));
            return "index";
        }

        costManager.addMonthsIncome(currYear, currMonth, income);
        return "redirect:/" + currYear + "/" + currMonth;
    }

    @PostMapping("/{currYear}/{currMonth}/expense")
    public String postExpense(Model model, @ModelAttribute @Valid Cost expense, BindingResult bindingResult, @PathVariable int currYear, @PathVariable int currMonth) {
        if(bindingResult.hasErrors()){
            model.addAttribute("expense", expense);
            prepareModel(model, YearMonth.of(currYear, currMonth));
            return "index";
        }


        CostTables costTables = costManager.getTablesOf(YearMonth.of(currYear, currMonth));
        costManager.addMonthsExp(currYear, currMonth, expense);
        model.addAttribute("expenses", costTables.getExpenses());
        return "redirect:/" + currYear + "/" + currMonth;
    }

    @PostMapping("/{currYear}/{currMonth}/fixedIncome")
    public String addFixedIncome(Model model, @ModelAttribute @Valid FixedCostForm festeEinname, BindingResult bindingResult, @PathVariable int currYear, @PathVariable int currMonth) {
        if(bindingResult.hasErrors()){
            model.addAttribute("fixedIncome", festeEinname);
            prepareModel(model, YearMonth.of(currYear, currMonth));
            return "indexMonth";
        }
        costManager.addToFixedIncome(festeEinname);
        return "redirect:/" + currYear + "/" + currMonth;
    }

    @PostMapping("/{currYear}/{currMonth}/fixedExpense")
    public String addFixedExp(Model model, @ModelAttribute @Valid FixedCostForm expense, BindingResult bindingResult, @PathVariable int currYear, @PathVariable int currMonth) {
        if(bindingResult.hasErrors()){
            model.addAttribute("fixedExpense", expense);
            prepareModel(model, YearMonth.of(currYear, currMonth));
            return "indexMonth";
        }
        if(expense.getFrequency() != Interval.MONTHLY) {
            potManager.addPot(
                    new PotForRegularExp(expense.getDescr(), expense.getStart().minusMonths(1), expense.getStart().minusMonths(1),
                    costManager.getMonthlyCost(expense), expense.getFrequency()));
        }
        costManager.addToFixedExp(expense);
        return "redirect:/" + currYear + "/" + currMonth;

    }

    @PostMapping("/{currYear}/{currMonth}/toPots")
    public String addToPots(Model model, @RequestParam double amount, @RequestParam String potSelect, @PathVariable int currYear, @PathVariable int currMonth) {
        BigDecimal amountD = new BigDecimal(amount);
        CostTables thisTables = costManager.getTablesOf(YearMonth.of(currYear, currMonth));

        if(potSelect.isEmpty()){
            costManager.addToPots(thisTables, potManager, amountD);
        } else {
            costManager.addToPot(thisTables, potManager, amountD, potSelect);
        }
        return "redirect:/" + currYear + "/" + currMonth;
    }

    @PostMapping("/{currYear}/{currMonth}/deleteFixedIncome")
    public String deleteFixedIncome(Model model, @RequestParam String descr, @RequestParam YearMonth start, @PathVariable int currYear, @PathVariable int currMonth) {
        costManager.deleteFromFixedIncome(descr, start);
        return "redirect:/" + currYear + "/" + currMonth;
    }

    @PostMapping("/{currYear}/{currMonth}/deleteFixedExpense")
    public String deleteFixedExpense(Model model, @RequestParam String descr, @RequestParam YearMonth start, @PathVariable int currYear, @PathVariable int currMonth) {
        costManager.deleteFromFixedExp(descr, start);
        return "redirect:/" + currYear + "/" + currMonth;
    }

    @PostMapping("/{currYear}/{currMonth}/deleteIncome")
    public String deleteIncome(Model model, @RequestParam Long id, @PathVariable int currYear, @PathVariable int currMonth) {
        costManager.deleteFromIncome(id, currYear, currMonth);
        return "redirect:/" + currYear + "/" + currMonth;
    }

    @PostMapping("/{currYear}/{currMonth}/deleteExp")
    public String deleteExp(Model model, @RequestParam Long id, @PathVariable int currYear, @PathVariable int currMonth) {
        costManager.deleteFromExp(id, currYear, currMonth);
        return "redirect:/" + currYear + "/" + currMonth;
    }

    private void prepareModel(Model model, YearMonth month) {
        // - - month navigation - - //
        int currMonth = month.getMonth().getValue();
        int currYear = month.getYear();

        MonthNavigation monthNavigation = new MonthNavigation(currYear, currMonth);

        model.addAttribute("currMonthString", month.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN));

        model.addAttribute("currMonth", currMonth);
        model.addAttribute("currYear", currYear);

        model.addAttribute("prevMonth", monthNavigation.prevMonth);
        model.addAttribute("prevMonthsYear", monthNavigation.prevMonthsYear);

        model.addAttribute("nextMonth", monthNavigation.nextMonth);
        model.addAttribute("nextMonthsYear", monthNavigation.nextMonthsYear);

        // - -  service data - - //
        model.addAttribute("pots", potManager.getPots());
        MonthData data = new MonthData(
                costManager.getAllMonthsIncome(month),
                costManager.getAllMonthsExp(month),
                costManager.getFixedIncome(),
                costManager.getFixedExp()
        );
        model.addAttribute("fixedIncomes", data.fixedIncome);
        model.addAttribute("fixedExpenses", data.fixedExpense);

        // Fallbacks für leere Felder bei neuem Aufruf oder Fehler
        if (!model.containsAttribute("income"))
            model.addAttribute("income", new Cost());
        if (!model.containsAttribute("expense"))
            model.addAttribute("expense", new Cost());
        if (!model.containsAttribute("fixedIncome"))
            model.addAttribute("fixedIncome", new FixedCost());
        if(!model.containsAttribute("fixedExpense"))
            model.addAttribute("fixedExpense", new FixedCost());

        /*CostTables costTables = costManager.getTablesOf(month);

        List<Cost> incomes = costManager.getMonthsIncomes(month);
        List<Cost> expenses = costManager.getMonthsExpenses(month);
        //hier ist Logik, die eig nicht in den Controller gehört(?)
        incomes.addAll(costTables.getIncomes());
        expenses.addAll(costTables.getExpenses());*/

        model.addAttribute("incomes", data.income);
        model.addAttribute("expenses", data.expense);

        // - - sums - - //
        MonthlySums monthlySums = costManager.calculateThisMonthsSums(month);
        model.addAttribute("sumIn", monthlySums.sumIn);
        model.addAttribute("sumOut", monthlySums.sumOut);
        model.addAttribute("difference", monthlySums.difference);
    }
}