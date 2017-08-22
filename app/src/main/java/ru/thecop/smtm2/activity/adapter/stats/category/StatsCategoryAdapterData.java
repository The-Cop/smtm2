package ru.thecop.smtm2.activity.adapter.stats.category;

import ru.thecop.smtm2.db.dto.CategoryStat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatsCategoryAdapterData {

    public static final StatsCategoryAdapterData EMPTY = new StatsCategoryAdapterData();

    private final List<StatsCategoryAdapterStatsInfo> categoryInfos;
    private final int periodDays;

    private StatsCategoryAdapterData() {
        categoryInfos = Collections.EMPTY_LIST;
        periodDays = 0;
    }

    public StatsCategoryAdapterData(List<CategoryStat> categoryStats, int periodDays) {
        this.categoryInfos = Collections.unmodifiableList(
                convertToAdapterInfo(categoryStats, defineMaxAmount(categoryStats)));
        this.periodDays = periodDays;
    }

    private static List<StatsCategoryAdapterStatsInfo> convertToAdapterInfo(List<CategoryStat> categoryStats,
                                                                            double amountMax) {
        List<StatsCategoryAdapterStatsInfo> result = new ArrayList<>(categoryStats.size());
        for (CategoryStat categoryStat : categoryStats) {
            result.add(new StatsCategoryAdapterStatsInfo(categoryStat, amountMax));
        }
        return result;
    }

    private static double defineMaxAmount(List<CategoryStat> categoryStats) {
        double max = 0d;
        for (CategoryStat categoryStat : categoryStats) {
            if (categoryStat.getTotalAmount() > max) {
                max = categoryStat.getTotalAmount();
            }
        }
        return max;
    }

    public List<StatsCategoryAdapterStatsInfo> getCategoryInfos() {
        return categoryInfos;
    }

    public int getPeriodDays() {
        return periodDays;
    }
}
