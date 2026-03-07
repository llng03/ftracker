package de.ftracker.controller;

import de.ftracker.domain.model.AppUser;
import de.ftracker.services.CurrentUserService;
import de.ftracker.services.dtos.StatisticsOverviewDTO;
import de.ftracker.services.dtos.dtoServices.StatisticsOverviewDTOService;
import de.ftracker.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/statistics")
@CrossOrigin(origins = "${app.frontend.url}")
public class StatisticsController {
    private final StatisticsService statisticsService;
    private final CurrentUserService currentUserService;
    private final StatisticsOverviewDTOService statisticsOverviewDTOService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService, StatisticsOverviewDTOService statisticsOverviewDTOService,
                                CurrentUserService currentUserService) {
        this.statisticsService = statisticsService;
        this.statisticsOverviewDTOService = statisticsOverviewDTOService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public StatisticsOverviewDTO getStatistics(Authentication authentication, @RequestParam int year, @RequestParam int month) {
        AppUser user = currentUserService.requireUser(authentication);
        return statisticsOverviewDTOService.getStatisticsOverviewDTO(year, month, user.getId());
    }

}
