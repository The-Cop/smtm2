package ru.thecop.smtm2.db;

import ru.thecop.smtm2.model.Category;

public class CategoryStatsRequestResult {
    private Category category;
    private double totalAmount;
    private int entriesCount;

    public CategoryStatsRequestResult(Category category, double totalAmount, int entriesCount) {
        this.category = category;
        this.totalAmount = totalAmount;
        this.entriesCount = entriesCount;
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

    public int getEntriesCount() {
        return entriesCount;
    }

    public void setEntriesCount(int entriesCount) {
        this.entriesCount = entriesCount;
    }
}
