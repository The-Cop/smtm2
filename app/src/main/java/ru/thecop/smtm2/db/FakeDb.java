package ru.thecop.smtm2.db;

import org.joda.time.LocalDateTime;
import ru.thecop.smtm2.model.Category;
import ru.thecop.smtm2.model.Spending;
import ru.thecop.smtm2.util.DateTimeConverter;

import java.util.*;

public class FakeDb {

    private static Map<Long, Category> categoryTable = new HashMap<>();
    private static Map<Long, Spending> spendingTable = new HashMap<>();

    static {
        fillCategories();
        fillSpendings();
    }

    public static Spending findSpendingById(Long id) {
        return spendingTable.get(id);
    }

    public static Category findCategoryById(Long id) {
        return categoryTable.get(id);
    }

    public static List<Category> findCategories() {
        return new ArrayList<>(categoryTable.values());
    }

    private static void fillSpendings() {
        createSpendingInTable(1L, new LocalDateTime(2017, 7, 12, 12, 21), 3.50, 1L);
        createSpendingInTable(2L, new LocalDateTime(2017, 7, 12, 13, 33), 666, 1L);
        createSpendingInTable(3L, new LocalDateTime(2017, 7, 13, 23, 59), 777.13, 1L);

        createSpendingInTable(4L, new LocalDateTime(2017, 7, 12, 0, 1), 63.55, 2L);
        createSpendingInTable(5L, new LocalDateTime(2017, 7, 12, 0, 3), 33.11, 2L);
        createSpendingInTable(6L, new LocalDateTime(2017, 7, 13, 0, 1), 36.6, 2L);

        createSpendingInTable(7L, new LocalDateTime(2017, 7, 12, 22, 17), 634, 3L);
        createSpendingInTable(8L, new LocalDateTime(2017, 7, 12, 14, 56), 335.1, 3L);
        createSpendingInTable(9L, new LocalDateTime(2017, 7, 13, 11, 3), 22.33, 3L);
    }

    private static void fillCategories() {
        createCategoryInTable(1L, "Продукты");
        createCategoryInTable(2L, "Электроника");
        createCategoryInTable(3L, "Кино");
    }

    private static Spending createSpendingInTable(Long id, LocalDateTime localDateTime, double amount, long categoryId) {
        if (findCategoryById(categoryId) == null) {
            throw new IllegalArgumentException("categoryId");
        }
        Spending s = new Spending();
        s.setId(id);
        s.setTimestamp(DateTimeConverter.convert(localDateTime));
        s.setAmount(amount);
        s.setCategoryId(categoryId);
        s.setUid(UUID.randomUUID().toString());
        spendingTable.put(id, s);
        return s;
    }

    private static Category createCategoryInTable(Long id, String name) {
        Category c = new Category();
        c.setId(id);
        c.setName(name);
        categoryTable.put(id, c);
        return c;
    }
}
