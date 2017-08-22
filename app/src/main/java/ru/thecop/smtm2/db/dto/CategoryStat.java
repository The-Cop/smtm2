package ru.thecop.smtm2.db.dto;

import ru.thecop.smtm2.model.Category;

public class CategoryStat {
    private final Category category;
    private final double totalAmount;
    private final int entriesCount;

    public CategoryStat(Category category, double totalAmount, int entriesCount) {
        this.category = category;
        this.totalAmount = totalAmount;
        this.entriesCount = entriesCount;
    }

    public Category getCategory() {
        return category;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public int getEntriesCount() {
        return entriesCount;
    }
}
