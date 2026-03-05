package de.ftracker.domain.model.cost;


import de.ftracker.domain.model.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.User;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="cost_type")
public class Cost {

    @Id
    @Column(unique=true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Beschreibung darf nicht leer sein")
    private String descr;

    @DecimalMin(value = "0.00", message = "Betrag darf nicht negativ sein")
    @NotNull
    private BigDecimal amount;

    private boolean isIncome;

    @ManyToOne
    private AppUser user;

    @ManyToOne
    private Category category;

    public Cost() {
        // Default-Konstruktor für Spring Binding
    }

    public Cost(Long id, String descr, BigDecimal amount, boolean isIncome, Category category, AppUser user) {
        this.id = id;
        this.descr = descr;
        this.amount = amount;
        this.user = user;
        this.isIncome = isIncome;
        this.category = category;
    }

    public Cost(String descr, BigDecimal amount, boolean isIncome, AppUser user, Category category) {
        this.descr = descr;
        this.amount = amount;
        this.isIncome = isIncome;
        this.user = user;
        this.category = category;
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

    public boolean isIncome() {
        return isIncome;
    }

    // equals() und hashCode() kannst du nur überschreiben, wenn nötig
}