package de.ftracker.domain.model.pots;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.cost.Interval;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Getter
@Setter
@DiscriminatorValue("REGULAR")
public class PotForRegularExp extends BudgetPot{
    private int lastSavedMonth;
    private int lastSavedYear;

    private int lastPayedMonth;
    private int lastPayedYear;

    private BigDecimal regularAmount;

    private Long fCostId;

    @Enumerated(EnumType.STRING)
    private Interval frequency;

    public PotForRegularExp() {}

    public PotForRegularExp(String name, AppUser user, YearMonth lastSaved, YearMonth lastPayed, BigDecimal regularAmount, Long costId, Interval frequency) {
        super(name, user);
        this.lastSavedMonth = lastSaved.getMonthValue();
        this.lastSavedYear = lastSaved.getYear();
        this.lastPayedMonth = lastPayed.getMonthValue();
        this.lastPayedYear = lastPayed.getYear();
        this.regularAmount = regularAmount;
        this.fCostId = costId;
        this.frequency = frequency;
    }

    public void update(YearMonth current) {
        while(lastSavedMonth != current.getMonthValue() || lastSavedYear != current.getYear()) {
            if(lastSavedMonth != 12){
                lastSavedMonth++;
            } else {
                lastSavedMonth = 1;
                lastSavedYear++;
            }
            addEntry(
                    YearMonth.of(lastSavedYear, lastSavedMonth).atDay(1),
                    regularAmount
            );
        }
    }

}
