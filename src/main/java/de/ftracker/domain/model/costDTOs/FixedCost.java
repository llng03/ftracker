package de.ftracker.domain.model.costDTOs;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

import static de.ftracker.domain.model.costDTOs.Interval.MONTHLY;

@Getter
@Setter
@Entity
@DiscriminatorValue("FIXED")
public class FixedCost extends Cost{

    @Enumerated(EnumType.STRING)
    private Interval frequency = MONTHLY;

    private int startMonth;

    private int startYear;

    private Integer endMonth;

    private Integer endYear;


    public FixedCost() {
        super("", BigDecimal.ZERO, true);
        this.frequency = Interval.MONTHLY; // oder null
        this.startMonth = YearMonth.now().getMonthValue();
        this.startYear = YearMonth.now().getYear();
        this.endMonth = null;
        this.endYear = null;
    }

    public FixedCost(String name, BigDecimal amount, boolean isIncome, Interval frequency, YearMonth start, YearMonth end) {
        super(name, amount, isIncome);
        this.frequency = frequency;
        this.startMonth = start.getMonthValue();
        this.startYear = start.getYear();
        if (end != null) {
            this.endMonth = end.getMonthValue();
            this.endYear = end.getYear();
        } else {
            this.endMonth = null;
            this.endYear = null;
        }
    }

    public void setStart(YearMonth start) {
        this.startMonth = start.getMonthValue();
        this.startYear = start.getYear();
    }

    public void setEnd(Optional<YearMonth> ym) {
        if (ym.isPresent()) {
            this.endYear = ym.get().getYear();
            this.endMonth = ym.get().getMonthValue();
        } else {
            this.endYear = null;
            this.endMonth = null;
        }
    }

    public YearMonth getStart() {
        return YearMonth.of(startYear, startMonth);
    }

    public Optional<YearMonth> getEnd() {
        return endYear == null ? Optional.empty() : Optional.of(YearMonth.of(endYear, endMonth));
    }

    public YearMonth getEndValue() {
        return getEnd().orElse(null);
    }

    @Override
    public boolean isFixedCost() {
        return true;
    }


    public void setIsIncome(boolean isIncome) {
        super.setIncome(isIncome);
    }
}
