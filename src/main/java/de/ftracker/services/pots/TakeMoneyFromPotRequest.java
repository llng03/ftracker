package de.ftracker.services.pots;

import de.ftracker.domain.model.potsDTOs.BudgetPot;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TakeMoneyFromPotRequest {
    private Long potId;
    @NotNull
    private BigDecimal amount;
}
