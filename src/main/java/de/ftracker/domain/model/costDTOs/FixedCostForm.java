package de.ftracker.domain.model.costDTOs;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

import static de.ftracker.domain.model.costDTOs.Interval.MONTHLY;

@Getter
@Setter
public class FixedCostForm {
    private String descr;

    private BigDecimal amount;

    private boolean isIncome;

    private Interval frequency = MONTHLY;

    private YearMonth start;

    private YearMonth end = null;

    public FixedCostForm() {
        this.descr = "";
        this.amount = BigDecimal.ZERO;
        this.isIncome = true;
        this.frequency = Interval.MONTHLY; // oder null
        this.start = YearMonth.now();
        this.end = null;
    }

    public FixedCostForm(String name, BigDecimal amount, boolean isIncome, Interval frequency, YearMonth start, YearMonth end) {
        this.descr = name;
        this.amount = amount;
        this.isIncome = isIncome;
        this.frequency = frequency;
        this.start = start;
        this.end = end;
    }

    public Optional<YearMonth> getEnd(){
        return Optional.ofNullable(end);
    }

    public boolean getIsIncome(){
        return isIncome;
    }
}
