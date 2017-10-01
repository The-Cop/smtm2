package ru.thecop.smtm2.activity.adapter.stats;

import org.joda.time.LocalDate;
import ru.thecop.smtm2.activity.adapter.stats.category.StatsCategoryLoaderResult;
import ru.thecop.smtm2.activity.adapter.stats.spending.StatsSpendingLoaderResult;
import ru.thecop.smtm2.db.ContextAndSessionHolder;
import ru.thecop.smtm2.db.DbHelper;
import ru.thecop.smtm2.db.dto.CategoryStat;
import ru.thecop.smtm2.model.Spending;
import ru.thecop.smtm2.util.DateTimeUtils;

import java.util.List;

//TODO test this
public class StatsLoaderHelper {

    public static StatsSpendingLoaderResult loadAllTimeSpendingsStats(ContextAndSessionHolder contextAndSessionHolder) {
        List<Spending> spendings = DbHelper.findAllConfirmedSpendings(contextAndSessionHolder);
        LocalDate dateFrom = defineDateFrom(spendings);
        LocalDate dateTo = defineDateTo(spendings);

        StatsSpendingLoaderResult result = new StatsSpendingLoaderResult(spendings, dateFrom, dateTo);
        return result;
    }

    public static StatsSpendingLoaderResult loadSpendingsStatsForDates(ContextAndSessionHolder contextAndSessionHolder,
                                                                       LocalDate dateFrom, LocalDate dateTo) {
        List<Spending> spendings = DbHelper.findConfirmedSpendings(
                DateTimeUtils.convert(DateTimeUtils.atStartOfDay(dateFrom)),
                DateTimeUtils.convert(DateTimeUtils.atEndOfDay(dateTo)),
                contextAndSessionHolder);

        return new StatsSpendingLoaderResult(spendings, dateFrom, dateTo);
    }

    public static StatsCategoryLoaderResult loadAllTimeCategoryStats(ContextAndSessionHolder contextAndSessionHolder) {
        long earliestSpendingTimestamp = findEarliestSpendingTimestamp(contextAndSessionHolder);
        if (earliestSpendingTimestamp < 0) {
            return StatsCategoryLoaderResult.EMPTY;
        }

        long latestSpendingTimestamp = findLatestSpendingTimestamp(contextAndSessionHolder);
        if (latestSpendingTimestamp < 0) {
            return StatsCategoryLoaderResult.EMPTY;
        }

        LocalDate definedDateFrom = DateTimeUtils.convert(earliestSpendingTimestamp).toLocalDate();
        LocalDate definedDateTo = DateTimeUtils.convert(latestSpendingTimestamp).toLocalDate();
        List<CategoryStat> categoryStats = DbHelper.loadCategoriesStats(
                earliestSpendingTimestamp,
                latestSpendingTimestamp,
                contextAndSessionHolder);

        return new StatsCategoryLoaderResult(categoryStats, definedDateFrom, definedDateTo);
    }

    public static StatsCategoryLoaderResult loadCategoryStatsForDates(ContextAndSessionHolder contextAndSessionHolder,
                                                                      LocalDate dateFrom, LocalDate dateTo) {
        List<CategoryStat> categoryStats = DbHelper.loadCategoriesStats(
                DateTimeUtils.convert(DateTimeUtils.atStartOfDay(dateFrom)),
                DateTimeUtils.convert(DateTimeUtils.atEndOfDay(dateTo)),
                contextAndSessionHolder);

        return new StatsCategoryLoaderResult(categoryStats, dateFrom, dateTo);
    }


    private static long findLatestSpendingTimestamp(ContextAndSessionHolder contextAndSessionHolder) {
        Spending latest = DbHelper.findLatestSpending(contextAndSessionHolder);
        if (latest == null) {
            return -1;
        }
        return latest.getTimestamp();
    }

    private static long findEarliestSpendingTimestamp(ContextAndSessionHolder contextAndSessionHolder) {
        Spending earliest = DbHelper.findEarliestSpending(contextAndSessionHolder);
        if (earliest == null) {
            return -1;
        }
        return earliest.getTimestamp();
    }

    private static LocalDate defineDateTo(List<Spending> spendings) {
        if (spendings.isEmpty()) {
            return LocalDate.now();
        }
        return DateTimeUtils.convert(spendings.get(0).getTimestamp()).toLocalDate();
    }

    private static LocalDate defineDateFrom(List<Spending> spendings) {
        if (spendings.isEmpty()) {
            return LocalDate.now();
        }
        return DateTimeUtils.convert(spendings.get(spendings.size() - 1).getTimestamp()).toLocalDate();
    }
}
