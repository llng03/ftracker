package de.ftracker.model.potsDTOs;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
public class UndistributedPotAmount {
    @Id
    private Long id = 1L; //only one entry

    @NotNull
    private BigDecimal undistributed = BigDecimal.ZERO;

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
}
