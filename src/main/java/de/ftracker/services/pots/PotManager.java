package de.ftracker.services.pots;

import de.ftracker.domain.model.potsDTOs.BudgetPot;
import de.ftracker.domain.model.potsDTOs.PotForRegularExp;
import de.ftracker.domain.model.potsDTOs.UndistributedPotAmount;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PotManager {

    private final PotRepository potRepository;
    private final PotSummaryRepository potSummaryRepository;

    private final UndistributedPotAmount potSummary;

    public PotManager(PotRepository potRepository, PotSummaryRepository potSummaryRepository){
        this.potRepository = potRepository;
        this.potSummaryRepository = potSummaryRepository;
        this.potSummary = potSummaryRepository.findById(1L)
                .orElseGet(() -> potSummaryRepository.save(new UndistributedPotAmount()));
    }

    public List<BudgetPot> getPots() {
        return potRepository.findAll().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public BigDecimal getUndistributed() {
        return potSummary.getUndistributed();
    }

    public void addPot(BudgetPot budgetPot) {
        potRepository.save(budgetPot);
    }

    public BudgetPot getPot(String name) {
        return potRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Kein Pot mit Namen: " + name));

    }

    @Transactional
    public void distribute(BigDecimal amount, String potName) {
        distribute(amount, getPot(potName));
    }

    @Transactional
    public void distribute(BigDecimal amount, BudgetPot pot) {
        BigDecimal undistributed = getUndistributed();
        if(undistributed.compareTo(amount) < 0) {
            throw new IllegalArgumentException("not enough undistributed amount");
        }
        potSummary.setUndistributed(undistributed.subtract(amount));
        potSummaryRepository.save(potSummary);
        addEntry(pot, LocalDate.now(), amount);
    }

    @Transactional
    public void distribute(@NotNull long potId, @NotNull BigDecimal amount) {
        BudgetPot pot = potRepository.findById(potId)
                .orElseThrow(() -> new IllegalArgumentException("Pot nicht gefunden: " + potId));
        distribute(amount, pot);
    }

    public void addEntry(BudgetPot pot, LocalDate date, BigDecimal amount) {
        pot.addEntry(LocalDate.now(), amount);
        potRepository.save(pot);
    }

    public void addToUndistributed(BigDecimal amount) {
        potSummary.addToUndistributed(amount);
        potSummaryRepository.save(potSummary);
    }

    @Transactional
    public void deletePotByName(String string) {
        BudgetPot pot = getPot(string);
        addToUndistributed(pot.sum());
        potRepository.delete(pot);
    }

    @Transactional
    public void deletePotById(Long potId) {
        BudgetPot pot = potRepository.findById(potId)
                .orElseThrow(() -> new IllegalArgumentException("Pot nicht gefunden: " + potId));
        addToUndistributed(pot.sum());
        potRepository.delete(pot);
    }

    public BigDecimal getTotal() {
        BigDecimal currentUndistributed = potSummary.getUndistributed();
        return currentUndistributed.add(sumAllPots());
    }

    public void update(PotForRegularExp pot, YearMonth curr) {
        pot.update(curr);
        potRepository.save(pot);
    }

    private BigDecimal sumAllPots() {
        return getPots().stream().map(BudgetPot::sum).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void pay(Long potId, LocalDate date, BigDecimal amount) {
        BudgetPot pot = potRepository.findById(potId)
                .orElseThrow(() -> new IllegalArgumentException("Pot nicht gefunden: " + potId));
        pay(pot, date, amount);
    }

    public void pay(BudgetPot pot, LocalDate date, BigDecimal amount) {
        pot.pay(date, amount);
        potRepository.save(pot);
        potSummaryRepository.save(potSummary);
    }


}