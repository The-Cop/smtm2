package ru.thecop.smtm2.activity.adapter.stats.category;

import org.joda.time.LocalDate;
import ru.thecop.smtm2.activity.adapter.stats.AbstractStatsLoaderResult;
import ru.thecop.smtm2.activity.adapter.stats.Totals;
import ru.thecop.smtm2.db.dto.CategoryStat;
import ru.thecop.smtm2.util.DateTimeUtils;

import java.util.List;

public class StatsCategoryLoaderResult extends AbstractStatsLoaderResult {

    public static final StatsCategoryLoaderResult EMPTY = new StatsCategoryLoaderResult();

    private final StatsCategoryAdapterData adapterData;

    private StatsCategoryLoaderResult() {
        super(Totals.EMPTY, LocalDate.now(), LocalDate.now());
        adapterData = StatsCategoryAdapterData.EMPTY;
    }

    public StatsCategoryLoaderResult(List<CategoryStat> categoryStats, LocalDate dateFrom, LocalDate dateTo) {
        super(new Totals(dateFrom, dateTo, categoryStats), dateFrom, dateTo);
        this.adapterData = new StatsCategoryAdapterData(categoryStats,
                DateTimeUtils.getPeriodBetweenDates(dateFrom, dateTo));
    }

    public StatsCategoryAdapterData getAdapterData() {
        return adapterData;
    }
}
