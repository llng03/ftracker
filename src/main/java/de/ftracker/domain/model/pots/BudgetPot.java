package de.ftracker.domain.model.pots;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.cost.Cost;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "pot_type")
public class BudgetPot {
    @Id
    @Column(unique=true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private AppUser user;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PotEntry> entries = new ArrayList<>();

    public BudgetPot(){}

    public BudgetPot(String name, AppUser user) {
        this.name = name;
        this.user = user;
        this.entries = new ArrayList<>();
    }

    public BigDecimal sum() {
        return entries.stream()
                .map(PotEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addEntry(LocalDate date, BigDecimal amount) {
        entries.add(new PotEntry(date, amount));
    }

    public void addEntry(LocalDate date, BigDecimal amount, Cost cost) {
        entries.add(new PotEntry(date, amount, cost));
    }

    public void pay(LocalDate date, BigDecimal amount) {
        addEntry(LocalDate.now(), amount.negate());
    }

    public void removeEntry(PotEntry potEntry) {
        entries.remove(potEntry);
    }

    public PotEntry getEntryById(Long entryId) {
        return entries.stream().filter(e -> e.getId().equals(entryId)).findFirst().orElseThrow(
                () -> new IllegalArgumentException("No such entry")
        );
    }
}
