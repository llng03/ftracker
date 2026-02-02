package de.ftracker.services.pots;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DistributeRequest {
    private long potId;

    @NotNull
    private BigDecimal amount;
}
