package de.ftracker.controller;

import de.ftracker.domain.model.potsDTOs.BudgetPot;
import de.ftracker.services.pots.PotManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PotController.class)
public class PotControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PotManager potManager;

    @Test
    @DisplayName("/pots wird erreicht")
    void test1() throws Exception{
        when(potManager.getPots()).thenReturn(List.of());
        when(potManager.getUndistributed()).thenReturn(BigDecimal.ZERO);

        mvc.perform(get("/pots"))
                .andExpect(status().isOk())
                .andExpect(view().name("pots"))
                .andExpect(model().attributeExists("pots"))
                .andExpect(model().attributeExists("undistributed"));

    }

    @Test
    @DisplayName("createNewPotRedirectsToPots")
    void test2() throws Exception{
        mvc.perform(post("/pots/new").param("name", "chess"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pots"));
        verify(potManager).addPot(any(BudgetPot.class));
    }

    @Test
    @DisplayName("distributeToPots updates correctly")
    void test3() throws Exception{
        mvc.perform(post("/pots/distribute")
                .param("potName", "technik")
                .param("amount", "100"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pots"));
        verify(potManager).distribute(
                argThat(amount -> amount.compareTo(BigDecimal.valueOf(100)) == 0),
                eq("technik")
        );
    }
}