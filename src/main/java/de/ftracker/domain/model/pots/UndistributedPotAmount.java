package de.ftracker.domain.model.pots;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.cost.Cost;
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
    @Column(name="user_id")
    private Long userId;

    @OneToOne(optional=false)
    @MapsId
    @JoinColumn(name="user_id")
    private AppUser user;

    @NotNull
    private BigDecimal undistributed = BigDecimal.ZERO;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Cost> associatedExpenses = new ArrayList<>();

    public UndistributedPotAmount() {}

    public UndistributedPotAmount(AppUser user, BigDecimal undistributed) {
        this.user = user;
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
