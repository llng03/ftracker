package de.ftracker.services;

import de.ftracker.domain.model.AppUser;
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

    public PotManager(PotRepository potRepository, PotSummaryRepository potSummaryRepository){
        this.potRepository = potRepository;
        this.potSummaryRepository = potSummaryRepository;
    }

    public List<BudgetPot> getPots(AppUser user) {
        return potRepository.findAll().stream()
                .filter(pot -> pot.getUser().getId().equals(user.getId()))
                .filter(Objects::nonNull)
                .collect(toList());
    }

    public BigDecimal getUndistributed(AppUser user) {
        return getPotSummary(user).getUndistributed();
    }

    public void addPot(BudgetPot budgetPot) {
        potRepository.save(budgetPot);
    }

    public void addPot(String name, AppUser user) {
        BudgetPot pot = new BudgetPot();
        pot.setName(name);
        pot.setUser(user);

        addPot(pot);
    }

    public BudgetPot getPot(String name) {
        return potRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Kein Pot mit Namen: " + name));

    }

    @Transactional
    public void distribute(BigDecimal amount, String potName, AppUser user) {
        distribute(amount, getPot(potName), user);
    }

    @Transactional
    public void distribute(BigDecimal amount, BudgetPot pot, AppUser user) {
        BigDecimal undistributed = getUndistributed(user);
        if(undistributed.compareTo(amount) < 0) {
            throw new IllegalArgumentException("not enough undistributed amount");
        }
        UndistributedPotAmount potAmount = getPotSummary(user);
        potAmount.setUndistributed(undistributed.subtract(amount));
        potSummaryRepository.save(potAmount);
        addEntry(pot, LocalDate.now(), amount);
    }

    @Transactional
    public void distribute(@NotNull long potId, @NotNull BigDecimal amount, AppUser user) {
        BudgetPot pot = potRepository.findById(potId)
                .orElseThrow(() -> new IllegalArgumentException("Pot nicht gefunden: " + potId));
        distribute(amount, pot, user);
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

    public void addToUndistributed(BigDecimal amount, AppUser user) {
        UndistributedPotAmount potAmount = getPotSummary(user);
        potAmount.addToUndistributed(amount);
        potSummaryRepository.save(potAmount);
    }

    @Transactional
    public void deletePotByName(String string, AppUser user) {
        BudgetPot pot = getPot(string);
        addToUndistributed(pot.sum(), user);
        potRepository.delete(pot);
    }

    @Transactional
    public void deletePotById(Long potId, AppUser user) {
        BudgetPot pot = potRepository.findById(potId)
                .orElseThrow(() -> new IllegalArgumentException("Pot nicht gefunden: " + potId));
        addToUndistributed(pot.sum(), user);
        potRepository.delete(pot);
    }

    public BigDecimal getTotal(AppUser user) {

        BigDecimal currentUndistributed = getPotSummary(user).getUndistributed();
        return currentUndistributed.add(sumAllPots(user));
    }

    public void update(PotForRegularExp pot, YearMonth curr) {
        pot.update(curr);
        potRepository.save(pot);
    }

    private BigDecimal sumAllPots(AppUser user) {
        return getPots(user).stream().map(BudgetPot::sum).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void pay(Long potId, LocalDate date, BigDecimal amount, AppUser user) {
        BudgetPot pot = potRepository.findById(potId)
                .orElseThrow(() -> new IllegalArgumentException("Pot nicht gefunden: " + potId));
        pay(pot, date, amount, user);
    }

    public void pay(BudgetPot pot, LocalDate date, BigDecimal amount, AppUser user) {
        pot.pay(date, amount);
        potRepository.save(pot);
        potSummaryRepository.save(getPotSummary(user));
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

    public void deletePotEntryWithCostId(Cost cost, AppUser user) {
        UndistributedPotAmount potSummary = getPotSummary(user);
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

    public List<Long> getExpenseIdsRaw(AppUser user) {
        return potSummaryRepository.findAssociatedExpenseIdsRaw(user.getId());
    }

    public void addCostToUndistributed(Cost cost, AppUser user) {
        UndistributedPotAmount potSummary = getPotSummary(user);
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

    public List<PotForRegularExp> getPotsForRegularExp(AppUser user) {
        return getPots(user)
                .stream()
                .filter(pot -> pot instanceof PotForRegularExp)
                .map(PotForRegularExp.class::cast)
                .toList();
    }

    public void updatePotsForReqularExp(List<Long> ids, AppUser user) {
        for( Long id : ids ) {
            List<PotForRegularExp> regularExpPots = getPotsForRegularExp(user);
            for(PotForRegularExp pot: regularExpPots) {
                if(pot.getFCostId() != null && pot.getFCostId().equals(id)) {
                    update(pot, YearMonth.now());
                }
            }
        }
    }

    public void decouplePots(Long id, AppUser user) {
        List<PotForRegularExp> regularExpPots = getPotsForRegularExp(user);
        for(PotForRegularExp pot: regularExpPots) {
            if(pot.getFCostId().equals(id)) {
                pot.setFCostId(null);
                potRepository.save(pot);
            }
        }

    }

    private UndistributedPotAmount getPotSummary (AppUser user) {
        return potSummaryRepository.findByUserId(user.getId()).orElseThrow(() -> new IllegalArgumentException("no user with id: " + user.getId()));
    }
}