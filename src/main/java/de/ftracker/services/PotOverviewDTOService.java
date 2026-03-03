package de.ftracker.services;

import de.ftracker.domain.model.AppUser;
import de.ftracker.services.dtos.PotOverviewDTO;
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

    public PotOverviewDTO getPotOverviewDTO(AppUser user) {
        List<Long> ids = costManager.getFCostsIdsWithNonMonthlyRegExp(user.getId());
        potManager.updatePotsForReqularExp(ids, user);
        PotOverviewDTO dto = new PotOverviewDTO();

        dto.setPots(potManager.getPots(user));
        dto.setUndistributed(potManager.getUndistributed(user));
        dto.setSumTotal(potManager.getTotal(user));

        return dto;
    }
}
