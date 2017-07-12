package ru.thecop.smtm2.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

public class DateTimeConverter {

    public static long convert(LocalDateTime localDateTime){
        DateTime utc = localDateTime.toDateTime(DateTimeZone.UTC);
        return utc.getMillis();
    }

    public static LocalDateTime convert(long utcMillis){
        DateTime utc = new DateTime(utcMillis,DateTimeZone.UTC);
        return utc.toLocalDateTime();
    }
}
