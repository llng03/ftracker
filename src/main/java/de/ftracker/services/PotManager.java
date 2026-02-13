package de.ftracker.services;

import de.ftracker.domain.model.costDTOs.Cost;
import de.ftracker.domain.model.potsDTOs.BudgetPot;
import de.ftracker.domain.model.potsDTOs.PotEntry;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class PotManager {

    private final PotRepository potRepository;
    private final PotSummaryRepository potSummaryRepository;

    private UndistributedPotAmount potSummary;

    public PotManager(PotRepository potRepository, PotSummaryRepository potSummaryRepository){
        this.potRepository = potRepository;
        this.potSummaryRepository = potSummaryRepository;
        this.potSummary = potSummaryRepository.findById(1L)
                .orElseGet(() -> potSummaryRepository.save(new UndistributedPotAmount()));
    }

    public List<BudgetPot> getPots() {
        return potRepository.findAll().stream()
                .filter(Objects::nonNull)
                .collect(toList());
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
        pot.addEntry(date, amount);
        potRepository.save(pot);
    }

    public void addEntry(BudgetPot pot, LocalDate date, BigDecimal amount, Cost cost) {
        pot.addEntry(date, amount, cost);
    }

    public void saveInRepo(BudgetPot pot) {
        potRepository.save(pot);
    }

    public void addToUndistributed(BigDecimal amount) {
        this.potSummary = potSummaryRepository.findById(1L).orElseThrow();
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

    public Optional<BudgetPot> getPotById(Long potId) {
        return potRepository.findById(potId);
    }

    public void deleteEntry(BudgetPot pot, PotEntry entry) {
        pot.removeEntry(entry);
        potRepository.save(pot);
    }

    public BudgetPot findPotById(Long potId) {
        return potRepository.findById(potId).orElseThrow( ()->
                new IllegalArgumentException("No pot found with id " + potId));
    }

    public void deletePotEntryWithCostId(Cost cost) {
        this.potSummary = potSummaryRepository.findById(1L).orElseThrow();
        boolean removed = potSummary.getAssociatedExpenses().removeIf(e -> Objects.equals(e.getId(), cost.getId()));
        if(removed) {
            potSummary.setUndistributed(potSummary.getUndistributed().subtract(cost.getAmount()));
        }
        potSummaryRepository.saveAndFlush(potSummary);

        Optional<BudgetPot> budPot = findPotWithEntryWithCostId(cost.getId());
        if(budPot.isPresent()) {
            BudgetPot pot = budPot.get();
            Optional<PotEntry> entry = findEntryWithCostId(cost.getId(), pot);
            entry.ifPresent(potEntry -> budPot.get().removeEntry(potEntry));
            potRepository.save(pot);
        }
        potSummary.getAssociatedExpenses().forEach(e -> System.out.println(e.getId() + e.getDescr()));
        System.out.println("[]");
        potSummaryRepository.getReferenceById(1L).getAssociatedExpenses().forEach(e -> System.out.println(e.getId() + e.getDescr()));
        System.out.println("REACHED END OF DELETE");
    }
    public Optional<BudgetPot> findPotWithEntryWithCostId(Long costId) {
        return potRepository.findAll()
                .stream()
                .filter(pot -> findEntryWithCostId(costId, pot).isPresent())
                .findAny();
    }

    public Optional<PotEntry> findEntryWithCostId(Long costId, BudgetPot pot) {
        Optional<PotEntry> entryOpt = pot.getEntries()
                .stream()
                .filter(e -> e.getCost() != null && Objects.equals(e.getCost().getId(), costId))
                .findAny();
        if(entryOpt.isPresent()) {
            System.out.println("FOUND ASS ENTRY --");
            System.out.println(entryOpt.get().toString());
        }
        return entryOpt;

    }

    public List<Long> getExpenseIdsRaw() {
        return potSummaryRepository.findAssociatedExpenseIdsRaw();
    }

    public void addCostToUndistributed(Cost cost) {
        potSummary.addAssociatedExpense(cost);
        potSummaryRepository.save(potSummary);
    }

    public void updateAssociatedPotEntry(Long costId, BigDecimal amount) {
        Optional<BudgetPot> budPot = findPotWithEntryWithCostId(costId);
        if(budPot.isPresent()) {
            BudgetPot pot = budPot.get();
            Optional<PotEntry> entry = findEntryWithCostId(costId, pot);
            entry.ifPresent(potEntry -> potEntry.setAmount(amount));
            potRepository.save(pot);
        }
    }

    public List<PotForRegularExp> getPotsForRegularExp() {
        return getPots()
                .stream()
                .filter(pot -> pot instanceof PotForRegularExp)
                .map(PotForRegularExp.class::cast)
                .toList();
    }

    public void updatePotsForReqularExp(List<Long> ids) {
        for( Long id : ids ) {
            List<PotForRegularExp> regularExpPots = getPotsForRegularExp();
            for(PotForRegularExp pot: regularExpPots) {
                if(pot.getFCostId() != null && pot.getFCostId().equals(id)) {
                    update(pot, YearMonth.now());
                }
            }
        }
    }

    public void decouplePots(Long id) {
        List<PotForRegularExp> regularExpPots = getPotsForRegularExp();
        for(PotForRegularExp pot: regularExpPots) {
            if(pot.getFCostId().equals(id)) {
                pot.setFCostId(null);
                potRepository.save(pot);
            }
        }

    }
}