package de.ftracker.services.DTOs;

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
