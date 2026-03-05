package de.ftracker.services.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateCostRequest {
    @NotNull
    private Long costId;

    private String descr;

    private BigDecimal amount;

    private String category;
}
