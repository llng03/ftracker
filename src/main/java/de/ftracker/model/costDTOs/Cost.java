package de.ftracker.model.costDTOs;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="cost_type")
public class Cost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Beschreibung darf nicht leer sein")
    private String descr;

    @DecimalMin(value = "0.00", message = "Betrag darf nicht negativ sein")
    @NotNull
    private BigDecimal amount;

    private boolean isIncome;

    public Cost() {
        // Default-Konstruktor für Spring Binding
    }

    public Cost(String descr, BigDecimal amount, boolean isIncome) {
        this.descr = descr;
        this.amount = amount;
        this.isIncome = isIncome;
    }

    @Override
    public String toString() {
        return "Cost[descr=" + descr + ", amount=" + amount + "]";
    }

    public boolean getIsIncome(){
        return isIncome;
    }

    public boolean isFixedCost() {
        return false;
    }

    // equals() und hashCode() kannst du nur überschreiben, wenn nötig
}