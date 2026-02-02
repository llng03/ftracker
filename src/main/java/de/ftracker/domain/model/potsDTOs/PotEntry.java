package de.ftracker.domain.model.potsDTOs;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class PotEntry {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @NotNull
    private BigDecimal amount;

    public PotEntry(){}

    public PotEntry(LocalDate date, BigDecimal amount) {
        this.date = date;
        this.amount = amount;
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
