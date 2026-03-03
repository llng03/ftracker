package de.ftracker.services.dtos;

import de.ftracker.domain.model.cost.Interval;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@Setter
public class UpdateFixedCostRequest {
    @NotNull
    private Long costId;

    private String descr;
    private BigDecimal amount;
    private Interval frequency;

    private YearMonth start;

    private YearMonth end;

}
