package ru.thecop.smtm2.activity.adapter.stats;

import org.joda.time.LocalDate;

public abstract class AbstractStatsLoaderResult {
    private final Totals totals;
    private final LocalDate dateFrom;
    private final LocalDate dateTo;

    protected AbstractStatsLoaderResult(Totals totals, LocalDate dateFrom, LocalDate dateTo) {
        this.totals = totals;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public Totals getTotals() {
        return totals;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }
}
