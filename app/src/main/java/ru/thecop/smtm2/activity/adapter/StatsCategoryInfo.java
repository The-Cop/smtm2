package ru.thecop.smtm2.activity.adapter;

import ru.thecop.smtm2.db.CategoryStatsRequestResult;
import ru.thecop.smtm2.model.Category;

public class StatsCategoryInfo {
    private Category category;
    private double totalAmount;
    private int periodDays;
    private int entriesCount;

    public StatsCategoryInfo(CategoryStatsRequestResult categoryStatsRequestResult, int periodDays) {
        this.category = categoryStatsRequestResult.getCategory();
        this.totalAmount = categoryStatsRequestResult.getTotalAmount();
        this.entriesCount = categoryStatsRequestResult.getEntriesCount();
        this.periodDays = periodDays;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getPeriodDays() {
        return periodDays;
    }

    public void setPeriodDays(int periodDays) {
        this.periodDays = periodDays;
    }

    public int getEntriesCount() {
        return entriesCount;
    }

    public void setEntriesCount(int entriesCount) {
        this.entriesCount = entriesCount;
    }

    public double getEntriesPerDay() {
        if (entriesCount == 0 || periodDays == 0) {
            return 0;
        }
        return ((double) entriesCount) / periodDays;
    }

    public double getPerDay() {
        return totalAmount / periodDays;
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
