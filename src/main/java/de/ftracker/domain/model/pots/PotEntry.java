package de.ftracker.domain.model.pots;

import de.ftracker.domain.model.cost.Cost;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class PotEntry {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @NotNull
    private BigDecimal amount;

    @OneToOne(optional = true, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "cost_id", nullable = true, unique= true)
    private Cost cost;

    public PotEntry(){}

    public PotEntry(LocalDate date, BigDecimal amount, Cost cost) {
        this.date = date;
        this.amount = amount;
        this.cost = cost;
    }

    public PotEntry(LocalDate date, BigDecimal amount) {
        this.date = date;
        this.amount = amount;
        this.cost = null;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }
}
