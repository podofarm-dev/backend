package com.mildo.dev.api.study.service.utils;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.YEARS;

public class RecentActivityUtils {

    private static final String YEAR_SUFFIX = "y";
    private static final String MONTH_SUFFIX = "M";
    private static final String DAY_SUFFIX = "d";
    private static final String HOUR_SUFFIX = "h";
    private static final String MINUTE_SUFFIX = "m";
    private static final String SECOND_SUFFIX = "s";

    public static String getSolvedBefore(LocalDateTime solvedAt, LocalDateTime now) {
        long duration;
        if ((duration = YEARS.between(solvedAt, now)) != 0L) {
            return duration + YEAR_SUFFIX;
        }
        if ((duration = MONTHS.between(solvedAt, now)) != 0L) {
            return duration + MONTH_SUFFIX;
        }
        if ((duration = DAYS.between(solvedAt, now)) != 0L) {
            return duration + DAY_SUFFIX;
        }
        if ((duration = HOURS.between(solvedAt, now)) != 0L) {
            return duration + HOUR_SUFFIX;
        }
        if ((duration = MINUTES.between(solvedAt, now)) != 0L) {
            return duration + MINUTE_SUFFIX;
        }
        return duration + SECOND_SUFFIX;
    }

}
