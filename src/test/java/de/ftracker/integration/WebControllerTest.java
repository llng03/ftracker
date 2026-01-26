package de.ftracker.integration;

import de.ftracker.controller.WebController;
import de.ftracker.domain.model.costDTOs.Cost;
import de.ftracker.domain.model.costDTOs.Interval;
import de.ftracker.services.CostManager;
import de.ftracker.domain.model.CostTables;
import de.ftracker.domain.model.costDTOs.FixedCost;
import de.ftracker.services.pots.PotManager;
import de.ftracker.utils.MonthlySums;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebController.class)
public class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    //mock makes problems, because call of the method getMonthsIncome returns null because costManager is mocked...
    @MockitoBean
    private CostManager costManager;

    @MockitoBean
    private PotManager potManager;

    @BeforeEach
    void setupStubs() {
        // Erzeuge ein leeres CostTables-Objekt
        CostTables fakeTables = new CostTables(YearMonth.of(2025, Month.JUNE));

        // Stub für alle Tests
        when(costManager.getTablesOf(any())).thenReturn(fakeTables);
        when(costManager.getMonthsIncome(any())).thenReturn(Collections.emptyList());
        when(costManager.getMonthsExp(any())).thenReturn(Collections.emptyList());
        when(costManager.getFixedIncome()).thenReturn(Collections.emptyList());
        when(costManager.getFixedExp()).thenReturn(Collections.emptyList());

        when(costManager.calculateThisMonthsSums(YearMonth.of(2025, Month.JUNE))).thenReturn(new MonthlySums(BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Test
    @DisplayName("index lädt mit initalen attributen")
    void test1() throws Exception {
        mockMvc.perform(get("/2025/6"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("income", "expense", "incomes", "expenses", "sumIn", "sumOut", "difference"))
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("einnahmeAbschicken funktioniert falls valid")
    void test2() throws Exception {
        mockMvc.perform(post("/2025/6/income")
                        .param("descr", "TestEinnahme")
                        .param("amount", "100.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/2025/6"));

    }

    @Test
    @DisplayName("einnahmeAbschicken funktioniert nicht falls nicht valid")
    void test3() throws Exception {
        mockMvc.perform(post("/2025/6/income")
                        .param("descr", "")
                        .param("amount", "-100.00"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("cost", "descr", "amount"))
                .andExpect(model().errorCount(2));
    }

    @Test
    @DisplayName("neue Eingabe wird im Model gespeichert")
    void test4() throws Exception {
        Cost cost = new Cost("TestEinnahme", new BigDecimal("1000"), true);
        when(costManager.getAllMonthsIncome(YearMonth.of(2025, Month.JUNE))).thenReturn(Collections.singletonList(cost));

        mockMvc.perform(get("/2025/6"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("incomes"))
                .andExpect(model().attribute("incomes", hasItem(
                        allOf(
                                hasProperty("descr", is("TestEinnahme")),
                                hasProperty("amount", is(new BigDecimal("1000")))
                        )
                )));
    }

    @Test
    @DisplayName("ausgabeAbschicken updatet das Model")
    void test5() throws Exception {

        mockMvc.perform(post("/2025/6/expense")
                        .param("descr", "TestAusgabe")
                        .param("amount", "50.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/2025/6"));
    }

    @Test
    @DisplayName("neue Ausgabe wird im Model gespeichert ")
    void test6() throws Exception {
        Cost exp = new Cost("TestAusgabe", new BigDecimal("50.00"), false);
        when(costManager.getAllMonthsExp(YearMonth.of(2025, Month.JUNE))).thenReturn(Collections.singletonList(exp));
        mockMvc.perform(get("/2025/6"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("expenses"))
                .andExpect(model().attribute("expenses", hasItem(
                        allOf(
                                hasProperty("descr", is("TestAusgabe")),
                                hasProperty("amount", is(new BigDecimal("50.00")))
                        )
                )));
    }


    @Test
    @DisplayName("festeAusgabeAbschicken funktioniert falls valid")
    void test7() throws Exception {

        mockMvc.perform(post("/2025/6/fixedExpense")
                        .param("descr", "Miete")
                        .param("amount", "330"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/2025/6"));

    }

    @Test
    @DisplayName("neue festeAusgabe landet im Model")
    void test8() throws Exception {
        FixedCost fixedCost = new FixedCost("Miete", new BigDecimal("330"), false, Interval.MONTHLY, YearMonth.of(2025, 6), null);
        when(costManager.getFixedExp()).thenReturn(Collections.singletonList(fixedCost));
        mockMvc.perform(get("/2025/6"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("fixedExpenses"))
                .andExpect(model().attribute("fixedExpenses", hasItem(
                        allOf(
                                hasProperty("descr", is("Miete")),
                                hasProperty("amount", is(new BigDecimal("330")))
                        )
                )));

    }

    @Test
    @DisplayName("deleteFixedIncome works")
    void test9() throws Exception {
        mockMvc.perform(post("/2025/6/deleteFixedIncome")
                        .param("descr", "TestEinnahme")
                        .param("start", "2025-01")) // YearMonth als String
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/2025/6"));

        verify(costManager).deleteFromFixedIncome("TestEinnahme", YearMonth.of(2025, 1));
    }


    @Test
    @DisplayName("addToPots with select empty works")
    void test10() throws Exception {
        CostTables mockTables = new CostTables(YearMonth.of(2025, 6));
        when(costManager.getTablesOf(YearMonth.of(2025, 6))).thenReturn(mockTables);

        mockMvc.perform(post("/2025/6/toPots")
                        .param("amount", "100.5")
                        .param("potSelect", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/2025/6"));

        verify(costManager).addToPots(mockTables, potManager, new BigDecimal("100.5"));
        verify(costManager, never()).addToPot(any(), any(), any(), anyString());
    }

    @Test
    @DisplayName("addToPots with not Empty potSelect")
    void test11() throws Exception {
        CostTables mockTables = new CostTables(YearMonth.of(2025, 6));
        when(costManager.getTablesOf(YearMonth.of(2025, 6))).thenReturn(mockTables);

        mockMvc.perform(post("/2025/6/toPots")
                        .param("amount", "200")
                        .param("potSelect", "MyPot"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/2025/6"));

        verify(costManager).addToPot(mockTables, potManager, new BigDecimal("200"), "MyPot");
        verify(costManager, never()).addToPots(any(), any(), any());
    }


}
