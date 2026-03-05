package de.ftracker.services.dtos;

import de.ftracker.domain.model.pots.BudgetPot;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class PotOverviewDTO {
    private List<BudgetPot> pots;
    private BigDecimal undistributed;
    private BigDecimal sumTotal;
}
