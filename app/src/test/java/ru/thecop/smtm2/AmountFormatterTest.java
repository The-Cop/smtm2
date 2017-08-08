package ru.thecop.smtm2;

import org.junit.Test;
import ru.thecop.smtm2.util.AmountFormatter;

import static junit.framework.TestCase.assertEquals;

public class AmountFormatterTest {

    @Test
    public void testAmountFormatter() {
        double d = 0;
        assertEquals("0.00", AmountFormatter.format(d));

        d = 1.5;
        assertEquals("1.50", AmountFormatter.format(d));

        d = 1000;
        assertEquals("1 000.00", AmountFormatter.format(d));

        d = 123456789.99;
        assertEquals("123 456 789.99", AmountFormatter.format(d));
    }
}
