package de.ftracker.utils;

import java.time.YearMonth;

public class MonthNavigation {
    public final int currYear;
    public final int currMonth;
    public final int nextMonthsYear;
    public final int nextMonth;
    public final int prevMonthsYear;
    public final int prevMonth;

    public MonthNavigation(int currYear, int currMonth) {
        this.currYear = currYear;
        this.currMonth = currMonth;
        this.nextMonthsYear = computeNextMonthsYear(currYear, currMonth);
        this.nextMonth = computeNextMonth(currMonth);
        this.prevMonthsYear = computePrevMonthsYear(currYear, currMonth);
        this.prevMonth = computePrevMonth(currMonth);
    }

    public MonthNavigation(YearMonth currYearMonth) {
        this(currYearMonth.getYear(), currYearMonth.getMonthValue());
    }

    private int computeNextMonthsYear(int currYear, int currMonth) {
        return (currMonth == 12 ? currYear + 1 : currYear);
    }

    private int computeNextMonth(int currMonth) {
        return (currMonth == 12 ? 1 : currMonth+1);
    }

    private int computePrevMonthsYear(int currYear, int currMonth) {
        return (currMonth == 1 ? currYear - 1 : currYear);
    }

    private int computePrevMonth(int currMonth) {
        return (currMonth == 1 ? 12 : currMonth-1);
    }

    public static int computeNumberOfMonthsPast(YearMonth start, YearMonth now) {
        int years = now.getYear() - start.getYear();
        int months = now.getMonthValue() - start.getMonthValue();
        return years * 12 + months;
    }

    private YearMonth computeNMonthsInFuture(YearMonth currYearMonth, int n) {
        int month = (currYearMonth.getMonthValue() + n) % 12;
        int yearsPast = (currYearMonth.getMonthValue() + n) / 12;
        int year = currYearMonth.getYear() + yearsPast;
        return YearMonth.of(year, month);
    }

    public YearMonth getPrevYearMonth() {
        return YearMonth.of(
                prevMonthsYear,
                prevMonth
        );
    }

    public YearMonth getNextYearMonth() {
        return YearMonth.of(
                nextMonthsYear,
                nextMonth
        );
    }
}
