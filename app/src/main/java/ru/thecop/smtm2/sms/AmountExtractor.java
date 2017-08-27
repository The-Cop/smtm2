package ru.thecop.smtm2.sms;


import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AmountExtractor {
    private static final String TAG = "AmountExtractor";

    private static final Pattern PATTERN_SUM = Pattern.compile(
            "(^| )" + //need something before the sum
                    "(\\d+([.,]\\d{2})?)" + //sum
                    "[^.:/\\-0-9]" // these must not follow the sum
    );

    public static AmountParseResult extractSum(String sms) {
        List<String> candidates = extractCandidates(sms);

        List<BigDecimal> sums = new ArrayList<>(candidates.size());
        for (String candidate : candidates) {
            sums.add(convertToBigDecimal(candidate));
        }

        if (sums.size() == 0) {
            Log.d(TAG, "Failed to extract sum from " + sms);
            return null;
        }

        //Sum usually goes first?
        BigDecimal bestFit = sums.get(0);

        return new AmountParseResult(bestFit, sums);

    }

    private static List<String> extractCandidates(String sms) {
        List<String> candidates = new ArrayList<>();
        Matcher m = PATTERN_SUM.matcher(sms);
        while (m.find()) {
            String sum = m.group(2);
            sum = sum.replace(',', '.');
            candidates.add(sum);
        }
        return candidates;
    }

    private static BigDecimal convertToBigDecimal(String sum) {
        try {
            return new BigDecimal(sum);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Failed to convert to BigDecimal: " + sum);
            e.printStackTrace();
        }
        return null;
    }

}
