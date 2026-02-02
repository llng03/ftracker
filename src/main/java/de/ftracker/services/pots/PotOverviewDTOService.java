package de.ftracker.services.pots;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PotOverviewDTOService {
    private final PotManager potManager;

    @Autowired
    public PotOverviewDTOService(PotManager potManager) {
        this.potManager = potManager;
    }

    public PotOverviewDTO getPotOverviewDTO() {
        PotOverviewDTO dto = new PotOverviewDTO();

        dto.setPots(potManager.getPots());
        dto.setUndistributed(potManager.getUndistributed());
        dto.setSumTotal(potManager.getTotal());

        return dto;
    }
}
