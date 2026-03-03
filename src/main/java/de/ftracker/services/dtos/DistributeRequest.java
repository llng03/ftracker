package de.ftracker.services.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DistributeRequest {
    private Long potId;

    @NotNull
    private BigDecimal amount;
}
