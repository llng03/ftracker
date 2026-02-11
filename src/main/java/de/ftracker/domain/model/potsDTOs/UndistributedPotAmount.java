package de.ftracker.domain.model.potsDTOs;

import de.ftracker.domain.model.costDTOs.Cost;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class UndistributedPotAmount {
    @Id
    private Long id = 1L; //only one entry

    @NotNull
    private BigDecimal undistributed = BigDecimal.ZERO;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Cost> associatedExpenses = new ArrayList<>();

    public UndistributedPotAmount() {}

    public BigDecimal getUndistributed() {
        return undistributed;
    }

    public void setUndistributed(BigDecimal undistributed) {
        this.undistributed = undistributed;
    }

    public void addToUndistributed(BigDecimal amount) {
        undistributed = undistributed.add(amount);
    }

    public void addAssociatedExpense(Cost cost) {
        associatedExpenses.add(cost);
    }

    public void removeAssociatedExpense(Cost cost) {
        associatedExpenses.remove(cost);
    }
}
