package ru.thecop.smtm2.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

public class DateTimeUtils {

    public static long convert(LocalDateTime localDateTime) {
        DateTime utc = localDateTime.toDateTime(DateTimeZone.UTC);
        return utc.getMillis();
    }

    public static LocalDateTime convert(long utcMillis) {
        DateTime utc = new DateTime(utcMillis, DateTimeZone.UTC);
        return utc.toLocalDateTime();
    }

    public static LocalDateTime atStartOfDay(LocalDate localDate) {
        return localDate.toLocalDateTime(LocalTime.MIDNIGHT);
    }

    public static LocalDateTime atEndOfDay(LocalDate localDate) {
        return localDate.toLocalDateTime(LocalTime.MIDNIGHT.minusMillis(1));
    }

    //TODO test
    public static int getPeriodBetweenDates(LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom == null || dateTo == null) {
            return 0;
        }
        return Days.daysBetween(dateFrom, dateTo).getDays() + 1;
    }

    public static int getPeriodBetweenDates(long timestampFrom, long timestampTo) {
        return getPeriodBetweenDates(convert(timestampFrom).toLocalDate(), convert(timestampTo).toLocalDate());
    }
}
