package ru.thecop.smtm2.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public final class AmountFormatter {
    private AmountFormatter() {
    }

    public static String format(double amount) {
        //todo implement
        DecimalFormat df = new DecimalFormat("###,##0.00");
        DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(symbols);
        return df.format(amount);
    }

}
