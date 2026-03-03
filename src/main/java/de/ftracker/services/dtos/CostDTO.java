package de.ftracker.services.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CostDTO {
    private String descr;
    private BigDecimal amount;
    private boolean income;
    private String category;
}
