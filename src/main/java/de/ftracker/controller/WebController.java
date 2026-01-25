package de.ftracker.controller;

import de.ftracker.services.CostManager;
import de.ftracker.model.CostTables;
import de.ftracker.model.costDTOs.*;
import de.ftracker.model.pots.PotForRegularExp;
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

    @PostMapping("/{currYear}/{currMonth}/einnahme")
    public String postIncome(Model model, @ModelAttribute @Valid Cost income, BindingResult bindingResult,
                             @PathVariable int currYear, @PathVariable int currMonth) {
        if(bindingResult.hasErrors()){
            model.addAttribute("einnahme", income);

            prepareModel(model, YearMonth.of(currYear, currMonth));
            return "index";
        }

        costManager.addIncome(currYear, currMonth, income);
        return "redirect:/" + currYear + "/" + currMonth;
    }

    @PostMapping("/{currYear}/{currMonth}/ausgabe")
    public String postExpense(Model model, @ModelAttribute @Valid Cost expense, BindingResult bindingResult, @PathVariable int currYear, @PathVariable int currMonth) {
        if(bindingResult.hasErrors()){
            model.addAttribute("ausgabe", expense);
            prepareModel(model, YearMonth.of(currYear, currMonth));
            return "index";
        }


        CostTables costTables = costManager.getTablesOf(YearMonth.of(currYear, currMonth));
        costManager.addExp(currYear, currMonth, expense);
        model.addAttribute("ausgaben", costTables.getExpenses());
        return "redirect:/" + currYear + "/" + currMonth;
    }

    @PostMapping("/{currYear}/{currMonth}/festeEinnahme")
    public String addFixedIncome(Model model, @ModelAttribute @Valid FixedCostForm festeEinname, BindingResult bindingResult, @PathVariable int currYear, @PathVariable int currMonth) {
        if(bindingResult.hasErrors()){
            model.addAttribute("festeEinnahme", festeEinname);
            prepareModel(model, YearMonth.of(currYear, currMonth));
            return "indexMonth";
        }
        costManager.addToFixedIncome(festeEinname);
        return "redirect:/" + currYear + "/" + currMonth;
    }

    @PostMapping("/{currYear}/{currMonth}/festeAusgabe")
    public String addFixedExp(Model model, @ModelAttribute @Valid FixedCostForm ausgabe, BindingResult bindingResult, @PathVariable int currYear, @PathVariable int currMonth) {
        if(bindingResult.hasErrors()){
            model.addAttribute("festeAusgabe", ausgabe);
            prepareModel(model, YearMonth.of(currYear, currMonth));
            return "indexMonth";
        }
        if(ausgabe.getFrequency() != Interval.MONTHLY) {
            potManager.addPot(new PotForRegularExp(ausgabe.getDescr(), ausgabe.getStart().minusMonths(1), ausgabe.getStart().minusMonths(1), costManager.getMonthlyCost(ausgabe), ausgabe.getFrequency()));
        }
        costManager.addToFixedExp(ausgabe);
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

    @PostMapping("/{currYear}/{currMonth}/deleteFixedEinnahme")
    public String deleteFixedEinnahme(Model model, @RequestParam String descr, @RequestParam YearMonth start, @PathVariable int currYear, @PathVariable int currMonth) {
        costManager.deleteFromFixedIncome(descr, start);
        return "redirect:/" + currYear + "/" + currMonth;
    }

    @PostMapping("/{currYear}/{currMonth}/deleteFixedAusgabe")
    public String deleteFixedAusgabe(Model model, @RequestParam String descr, @RequestParam YearMonth start, @PathVariable int currYear, @PathVariable int currMonth) {
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
        model.addAttribute("festeEinnahmen", data.fixedIncome);
        model.addAttribute("festeAusgaben", data.fixedExpense);

        // Fallbacks für leere Felder bei neuem Aufruf oder Fehler
        if (!model.containsAttribute("einnahme"))
            model.addAttribute("einnahme", new Cost());
        if (!model.containsAttribute("ausgabe"))
            model.addAttribute("ausgabe", new Cost());
        if (!model.containsAttribute("festeEinnahme"))
            model.addAttribute("festeEinnahme", new FixedCost());
        if(!model.containsAttribute("festeAusgabe"))
            model.addAttribute("festeAusgabe", new FixedCost());

        /*CostTables costTables = costManager.getTablesOf(month);

        List<Cost> einnahmen = costManager.getMonthsEinnahmen(month);
        List<Cost> ausgaben = costManager.getMonthsAusgaben(month);
        //hier ist Logik, die eig nicht in den Controller gehört(?)
        einnahmen.addAll(costTables.getEinnahmen());
        ausgaben.addAll(costTables.getAusgaben());*/

        model.addAttribute("einnahmen", data.income);
        model.addAttribute("ausgaben", data.expense);

        // - - sums - - //
        MonthlySums monthlySums = costManager.calculateThisMonthsSums(month);
        model.addAttribute("summeIn", monthlySums.sumIn);
        model.addAttribute("summeOut", monthlySums.sumOut);
        model.addAttribute("differenz", monthlySums.difference);
    }
}