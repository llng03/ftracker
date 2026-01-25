package de.ftracker.model;

import de.ftracker.model.costDTOs.Cost;
import de.ftracker.services.pots.PotManager;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class CostTables {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int month;
    private int year;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Cost> incomes;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Cost> expenses;

    public CostTables() {
        this.month = YearMonth.now().getMonthValue();
        this.year = YearMonth.now().getYear();
        this.incomes = new ArrayList<>();
        this.expenses = new ArrayList<>();
    }

    public CostTables(YearMonth yearMonth) {
        this.month = yearMonth.getMonthValue();
        this.year = yearMonth.getYear();
        this.incomes = new ArrayList<>();
        this.expenses = new ArrayList<>();
    }

    public List<Cost> getIncomes() {
        return incomes;
    }

    public List<Cost> getExpenses() {
        return expenses;
    }

    public YearMonth getYearMonth() {
        return YearMonth.of(this.year, this.month);
    }

    public void setYearMonth(YearMonth yearMonth) {
        this.month = yearMonth.getMonthValue();
        this.year = yearMonth.getYear();
    }

    public void setIncomes(List<Cost> einnahmen) {this.incomes = einnahmen;}

    public void setExpenses(List<Cost> ausgaben) {
        this.expenses = ausgaben;
    }

    public void addCostToIncomes(Cost cost){
        this.incomes.add(cost);
    }

    public void addCostToExpenses(Cost cost){
        this.expenses.add(cost);
    }

    public void addCostToExpenses(String name, BigDecimal amount) {
        addCostToExpenses(new Cost(name, amount, false));
    }

    public void addToPots(PotManager potManger, BigDecimal amount) {
        addCostToExpenses("auf Pots zu Verteilen", amount);
        potManger.addToUndistributed(amount);
    }

    public void addToPot(PotManager potManager, BigDecimal amount, String potName) {
        addCostToExpenses("auf Pot " + potName + " verteilen", amount);
        potManager.addToUndistributed(amount);
        potManager.distribute(amount, potName);
    }

    public BigDecimal sumEinnahmen() {
        return sum(incomes);
    }

    public BigDecimal sumAusgaben() {
        return sum(expenses);
    }

    private static BigDecimal sum(List<Cost> costs) {
        return costs.stream()
                .map(e -> e.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
