package de.ftracker.controller;

import de.ftracker.services.dtos.StatisticsOverviewDTO;
import de.ftracker.services.StatisticsOverviewDTOService;
import de.ftracker.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/statistics")
@CrossOrigin(origins = "http://localhost:5173")
public class StatisticsController {
    private final StatisticsService statisticsService;
    private final StatisticsOverviewDTOService statisticsOverviewDTOService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService, StatisticsOverviewDTOService statisticsOverviewDTOService) {
        this.statisticsService = statisticsService;
        this.statisticsOverviewDTOService = statisticsOverviewDTOService;
    }

    @GetMapping
    public StatisticsOverviewDTO getStatistics(@RequestParam int year, @RequestParam int month, @RequestParam Long userId) {
        return statisticsOverviewDTOService.getStatisticsOverviewDTO(year, month, userId);
    }

}
