package de.ftracker.services.DTOs;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class StatisticsOverviewDTO {
    private Map<String, BigDecimal> costSumPerCategory;
}
