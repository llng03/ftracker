package de.ftracker.unit;

import de.ftracker.domain.model.potsDTOs.BudgetPot;
import de.ftracker.domain.model.potsDTOs.UndistributedPotAmount;
import de.ftracker.services.pots.PotManager;
import de.ftracker.services.pots.PotRepository;
import de.ftracker.services.pots.PotSummaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PotManagerTest {

    @Mock
    PotRepository potRepository;

    @Mock
    PotSummaryRepository potSummaryRepository;

    PotManager manager;
    @BeforeEach
    void setupUndistributed() {
        UndistributedPotAmount potSummary = new UndistributedPotAmount();
        when(potSummaryRepository.findById(1L)).thenReturn(Optional.of(potSummary));
        manager = new PotManager(potRepository, potSummaryRepository);
    }

    @Test
    @DisplayName("addPot works")
    void test1() {
        BudgetPot pot = new BudgetPot("urlaub");
        when(potRepository.findByName("urlaub")).thenReturn(Optional.of(pot));
        assertThat(manager.getPot("urlaub")).isEqualTo(pot);
    }

    @Test
    @DisplayName("distributed correctly")
    void test2() {
        setupUndistributed();
        BudgetPot pot = new BudgetPot("ausflug");
        when(potRepository.findByName("ausflug")).thenReturn(Optional.of(pot));

        manager.addToUndistributed(new BigDecimal("500"));
        manager.distribute(new BigDecimal("150"), "ausflug");

        assertThat(new BigDecimal("350")).isEqualByComparingTo(manager.getUndistributed());
        assertThat(new BigDecimal("150")).isEqualByComparingTo(pot.sum());
    }

    @Test
    @DisplayName("distribute more than available throws Exception")
    void test3() {
        manager.addToUndistributed(new BigDecimal("50"));
        manager.addPot(new BudgetPot("new"));
        assertThrows(IllegalArgumentException.class, () -> manager.distribute(new BigDecimal("100"), "new"));
    }


    @Test
    @DisplayName("new pot has zero sum")
    void test4() {
        BudgetPot pot = new BudgetPot("urlaub");
        when(potRepository.findByName("urlaub")).thenReturn(Optional.of(pot));
        assertThat(manager.getPot("urlaub").sum()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("addToUndistributed increasesValue")
    void test5() {
        manager.addToUndistributed(new BigDecimal("500"));
        assertThat(manager.getUndistributed()).isEqualByComparingTo("500");
    }

    @Test
    @DisplayName("distributeToPot decreasesUnverteilt")
    void test6() {
        BudgetPot pot = new BudgetPot("technik");
        when(potRepository.findByName("technik")).thenReturn(Optional.of(pot));
        UndistributedPotAmount undistributedPotAmount = potSummaryRepository.findById(1L).orElse(null);
        undistributedPotAmount.setUndistributed(new BigDecimal("300"));

        manager.distribute(new BigDecimal("100"), "technik");
        assertThat(manager.getUndistributed()).isEqualByComparingTo("200");
        assertThat(pot.sum()).isEqualByComparingTo("100");
    }

}