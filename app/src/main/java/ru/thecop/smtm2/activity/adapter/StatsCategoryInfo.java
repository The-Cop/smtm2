package ru.thecop.smtm2.activity.adapter;

import ru.thecop.smtm2.db.CategoryStatsRequestResult;
import ru.thecop.smtm2.model.Category;

public class StatsCategoryInfo {
    private final CategoryStatsRequestResult categoryStatsRequestResult;
    private final int periodDays;

    public StatsCategoryInfo(CategoryStatsRequestResult categoryStatsRequestResult, int periodDays) {
        this.categoryStatsRequestResult = categoryStatsRequestResult;
        this.periodDays = periodDays;
    }

    public Category getCategory() {
        return categoryStatsRequestResult.getCategory();
    }

    public double getTotalAmount() {
        return categoryStatsRequestResult.getTotalAmount();
    }

    public int getPeriodDays() {
        return periodDays;
    }

    public int getEntriesCount() {
        return categoryStatsRequestResult.getEntriesCount();
    }

    public double getEntriesPerDay() {
        if (categoryStatsRequestResult.getEntriesCount() == 0 || periodDays == 0) {
            return 0;
        }
        return ((double) categoryStatsRequestResult.getEntriesCount()) / periodDays;
    }

    public double getPerDay() {
        return categoryStatsRequestResult.getTotalAmount() / periodDays;
    }

    public double getPerWeek() {
        return getPerDay() * 7;
    }

    public double getPerMonth() {
        return getPerDay() * 30;
    }

    public double getPerYear() {
        return getPerDay() * 365;
    }
}
