package de.ftracker.services.DTOs;

import de.ftracker.domain.model.potsDTOs.BudgetPot;
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
