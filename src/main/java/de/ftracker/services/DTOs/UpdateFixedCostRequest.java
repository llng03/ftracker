package de.ftracker.services.DTOs;

import de.ftracker.domain.model.costDTOs.Interval;
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
