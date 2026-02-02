package de.ftracker.services.pots;

import de.ftracker.domain.model.potsDTOs.BudgetPot;
import de.ftracker.domain.model.potsDTOs.UndistributedPotAmount;
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
