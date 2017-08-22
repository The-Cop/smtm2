package ru.thecop.smtm2.activity.adapter.stats.category;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import ru.thecop.smtm2.db.dto.CategoryStat;
import ru.thecop.smtm2.model.Category;

public class StatsCategoryAdapterStatsInfo {
    private final CategoryStat categoryStat;
    private final LinearLayout.LayoutParams shareLayoutParams;
    private final LinearLayout.LayoutParams oppositeShareLayoutParams;

    public StatsCategoryAdapterStatsInfo(CategoryStat categoryStat, double maxAmount) {
        this.categoryStat = categoryStat;
        double amountShareViewWeight = categoryStat.getTotalAmount() / maxAmount;
        shareLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                (1f - (float) amountShareViewWeight));

        oppositeShareLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                (float) amountShareViewWeight);
    }

    public Category getCategory() {
        return categoryStat.getCategory();
    }

    public double getTotalAmount() {
        return categoryStat.getTotalAmount();
    }

    public int getEntriesCount() {
        return categoryStat.getEntriesCount();
    }

    public double getEntriesPerDay(int periodDays) {
        if (categoryStat.getEntriesCount() == 0 || periodDays == 0) {
            return 0;
        }
        return ((double) categoryStat.getEntriesCount()) / periodDays;
    }

    public double getPerDay(int periodDays) {
        if (periodDays == 0) {
            return 0;
        }
        return categoryStat.getTotalAmount() / periodDays;
    }

    public double getPerWeek(int periodDays) {
        return getPerDay(periodDays) * 7;
    }

    public double getPerMonth(int periodDays) {
        return getPerDay(periodDays) * 30;
    }

    public double getPerYear(int periodDays) {
        return getPerDay(periodDays) * 365;
    }

    public LinearLayout.LayoutParams getShareLayoutParams() {
        return shareLayoutParams;
    }

    public LinearLayout.LayoutParams getOppositeShareLayoutParams() {
        return oppositeShareLayoutParams;
    }
}
