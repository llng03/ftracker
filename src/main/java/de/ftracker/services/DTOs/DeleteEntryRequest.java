package de.ftracker.services.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteEntryRequest {
    @NotNull
    private Long potId;
    @NotNull
    private Long entryId;
}
