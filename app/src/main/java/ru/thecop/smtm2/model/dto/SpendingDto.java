package ru.thecop.smtm2.model.dto;

import ru.thecop.smtm2.model.Category;
import ru.thecop.smtm2.model.Spending;

//TODO get rid (?) of DTOs when actual ORM lib is used
public class SpendingDto {
    private Category category;
    private Spending spending;

    public SpendingDto(Category category, Spending spending) {
        this.category = category;
        this.spending = spending;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Spending getSpending() {
        return spending;
    }

    public void setSpending(Spending spending) {
        this.spending = spending;
    }
}
