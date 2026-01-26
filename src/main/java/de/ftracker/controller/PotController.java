package de.ftracker.controller;

import de.ftracker.model.potsDTOs.BudgetPot;
import de.ftracker.model.potsDTOs.PotForRegularExp;
import de.ftracker.services.pots.PotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Controller
public class PotController {
    private final PotManager potManager;

    @Autowired
    public PotController(PotManager potManager) {
        this.potManager = potManager;
    }

    @GetMapping("/pots")
    public String pots(Model model) {
        prepareModel(model, YearMonth.now());
        return "pots";
    }

    @PostMapping("/pots/new")
    public String createNewPot(Model model, @RequestParam("name") String newPotName) {
        potManager.addPot(new BudgetPot(newPotName));
        model.addAttribute("pots", potManager.getPots());
        return "redirect:/pots";
    }

    @PostMapping("/pots/distribute")
    public String distribute(Model model, @RequestParam("potName") String potName,
                             @RequestParam("amount") double amount) {
        try {
            potManager.distribute(BigDecimal.valueOf(amount), potName);
        } catch(IllegalArgumentException e) {
            model.addAttribute("showDistributeModal", true);
            model.addAttribute("error", "Es wurde eine höhere Summe verteilt, als vorhanden ist :c");
            model.addAttribute("pots", potManager.getPots());
            return "pots";
        }
        model.addAttribute("pots", potManager.getPots());
        return "redirect:/pots";
    }

    @PostMapping("/pots/deletePot")
    public String deletePot(Model model, @RequestParam String potName) {
        potManager.deletePotByName(potName);
        return "redirect:/pots";
    }

    @PostMapping("/pots/pay")
    public String pay(Model model, @RequestParam("potName") String potName, @RequestParam("payAmount") double payAmount) {
        BudgetPot pot = potManager.getPot(potName);
        potManager.pay(pot, LocalDate.now(), new BigDecimal(payAmount));
        //pot.pay(LocalDate.now(), new BigDecimal(payAmount));
        return "redirect:/pots";
    }

    private void prepareModel(Model model, YearMonth curr) {
        model.addAttribute("pots", potManager.getPots());
        model.addAttribute("undistributed", potManager.getUndistributed());
        model.addAttribute("sumAll", potManager.getTotal());
        for(BudgetPot pot: potManager.getPots()) {
            if(pot instanceof PotForRegularExp){
                potManager.update((PotForRegularExp) pot,curr);
            }
        }

    }
}
