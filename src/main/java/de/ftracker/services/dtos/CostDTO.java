package de.ftracker.services.DTOs;

import de.ftracker.domain.model.costDTOs.Category;
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
