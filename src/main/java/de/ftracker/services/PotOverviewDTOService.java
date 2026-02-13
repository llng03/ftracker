package de.ftracker.services;

import de.ftracker.services.DTOs.PotOverviewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PotOverviewDTOService {
    private final PotManager potManager;
    private final CostManager costManager;

    @Autowired
    public PotOverviewDTOService(PotManager potManager, CostManager costManager) {
        this.costManager = costManager;
        this.potManager = potManager;
    }

    public PotOverviewDTO getPotOverviewDTO() {
        List<Long> ids = costManager.getFCostsIdsWithNonMonthlyRegExp();
        potManager.updatePotsForReqularExp(ids);
        PotOverviewDTO dto = new PotOverviewDTO();

        dto.setPots(potManager.getPots());
        dto.setUndistributed(potManager.getUndistributed());
        dto.setSumTotal(potManager.getTotal());

        return dto;
    }
}
