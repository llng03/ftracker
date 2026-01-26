package de.ftracker.unit;


import de.ftracker.model.CostTables;
import de.ftracker.model.costDTOs.Cost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CostTablesTest {
    @Test
    @DisplayName("table increases size")
    void test1() {
        CostTables tables = new CostTables();
        tables.addCostToExpenses(new Cost("Test", new BigDecimal("100"), false));
        assertEquals(1, tables.getExpenses().size());
    }

    @Test
    @DisplayName("returns correct sum")
    void test2() {
        CostTables tables = new CostTables();
        tables.addCostToExpenses(new Cost("Test", new BigDecimal("10.32"), false));
        tables.addCostToExpenses(new Cost("Test", new BigDecimal("20.27"), false));
        assertThat(tables.sumExpenses()).isEqualByComparingTo(new BigDecimal("30.59"));
    }
}