package ru.thecop.smtm2.sms;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class AmountParseResult {
    private BigDecimal bestFit;
    private List<BigDecimal> allSums;

    public AmountParseResult(BigDecimal bestFit, List<BigDecimal> allSums) {
        this.bestFit = bestFit;
        this.allSums = Collections.unmodifiableList(allSums);
    }

    public BigDecimal getBestFit() {
        return bestFit;
    }

    public List<BigDecimal> getAllSums() {
        return allSums;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AmountParseResult{");
        sb.append("bestFit=").append(bestFit);
        sb.append(", allSums=").append(allSums);
        sb.append('}');
        return sb.toString();
    }
}
