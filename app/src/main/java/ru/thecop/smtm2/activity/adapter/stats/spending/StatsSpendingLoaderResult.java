package ru.thecop.smtm2.activity.adapter.stats.spending;

import org.joda.time.LocalDate;
import ru.thecop.smtm2.activity.adapter.stats.AbstractStatsLoaderResult;
import ru.thecop.smtm2.activity.adapter.stats.Totals;
import ru.thecop.smtm2.model.Spending;

import java.util.Collections;
import java.util.List;

public class StatsSpendingLoaderResult extends AbstractStatsLoaderResult {

    private final List<Spending> spendings;

    public StatsSpendingLoaderResult(List<Spending> spendings, LocalDate dateFrom, LocalDate dateTo) {
        super(new Totals(spendings, dateFrom, dateTo), dateFrom, dateTo);
        this.spendings = Collections.unmodifiableList(spendings);
    }

    public List<Spending> getSpendings() {
        return spendings;
    }
}
