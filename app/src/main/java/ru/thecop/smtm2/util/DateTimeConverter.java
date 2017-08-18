package ru.thecop.smtm2.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

public class DateTimeConverter {

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
}
