package de.ftracker.utils;

import de.ftracker.domain.model.costDTOs.Interval;

public class IntervalCount {

    public static int countMonths(Interval interval) {
        int months;
        switch (interval) {
            case ANNUAL -> months = 12;
            case SEMI_ANNUAL -> months = 6;
            case QUARTERLY -> months = 3;
            default -> months = 1;
        }
        return months;
    }
}
