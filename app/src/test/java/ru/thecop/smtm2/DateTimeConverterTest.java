package ru.thecop.smtm2;

import org.joda.time.LocalDateTime;
import org.junit.Test;
import ru.thecop.smtm2.util.DateTimeUtils;

import static junit.framework.TestCase.assertEquals;

public class DateTimeConverterTest {

    @Test
    public void testConvertToMillis() {
        LocalDateTime ldt = new LocalDateTime(2017, 7, 15, 20, 59);
        long expectedMillis = 1500152340000L;
        assertEquals(expectedMillis, DateTimeUtils.convert(ldt));
    }

    @Test
    public void testConvertToLocalDateTime() {
        long millis = 1500152340000L;
        LocalDateTime expectedLdt = new LocalDateTime(2017, 7, 15, 20, 59);
        assertEquals(expectedLdt, DateTimeUtils.convert(millis));
    }

    @Test
    public void testDoubleConvert() {
        LocalDateTime ldt = new LocalDateTime(2017, 7, 15, 20, 59);
        assertEquals(ldt, DateTimeUtils.convert(DateTimeUtils.convert(ldt)));
    }
}
