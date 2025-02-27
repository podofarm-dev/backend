package com.podofarm.dev.api.study.service.utils;

import java.time.LocalDateTime;

import static com.podofarm.dev.global.exception.message.ExceptionMessage.PROBLEM_LEVEL_TYPE_MISMATCH;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.YEARS;

public class DashBoardUtils {

    private static final String YEAR_SUFFIX = "y";
    private static final String MONTH_SUFFIX = "M";
    private static final String DAY_SUFFIX = "d";
    private static final String HOUR_SUFFIX = "h";
    private static final String MINUTE_SUFFIX = "m";
    private static final String SECOND_SUFFIX = "s";

    private static final String LEVEL_REGEX = "-";

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

    public static Integer getLevel(String levelString) {
        String[] split = levelString.split(LEVEL_REGEX);
        if (split.length != 2) {
            throw new IllegalStateException(PROBLEM_LEVEL_TYPE_MISMATCH);
        }

        return Integer.parseInt(split[1]);
    }

}
