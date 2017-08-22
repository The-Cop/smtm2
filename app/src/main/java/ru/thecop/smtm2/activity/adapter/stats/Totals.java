package ru.thecop.smtm2.activity.adapter.stats;

import org.joda.time.LocalDate;
import ru.thecop.smtm2.db.dto.CategoryStat;
import ru.thecop.smtm2.model.Spending;
import ru.thecop.smtm2.util.DateTimeUtils;

import java.util.List;

public class Totals {

    public static final Totals EMPTY = new Totals();

    private int entriesCount;
    private int periodDays;
    private double sum;

    private Totals() {
    }

    public Totals(List<Spending> spendings, LocalDate dateFrom, LocalDate dateTo) {
        this.periodDays = DateTimeUtils.getPeriodBetweenDates(dateFrom, dateTo);
        this.entriesCount = spendings.size();
        sum = 0d;
        for (Spending spending : spendings) {
            sum += spending.getAmount();
        }
    }

    public Totals(LocalDate dateFrom, LocalDate dateTo, List<CategoryStat> categoryStats) {
        this.periodDays = DateTimeUtils.getPeriodBetweenDates(dateFrom, dateTo);
        for (CategoryStat category : categoryStats) {
            this.entriesCount += category.getEntriesCount();
            this.sum += category.getTotalAmount();
        }
    }

    public int getEntriesCount() {
        return entriesCount;
    }

    public int getPeriodDays() {
        return periodDays;
    }

    public double getSum() {
        return sum;
    }

    public double getEntriesPerDay() {
        if (entriesCount == 0 || periodDays == 0) {
            return 0;
        }
        return ((double) entriesCount) / periodDays;
    }

    public double getPerDay() {
        if (periodDays == 0) {
            return 0;
        }
        return sum / periodDays;
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
