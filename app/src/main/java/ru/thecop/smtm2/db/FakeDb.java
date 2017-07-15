package ru.thecop.smtm2.db;

import android.util.Log;
import org.joda.time.LocalDateTime;
import ru.thecop.smtm2.model.Category;
import ru.thecop.smtm2.model.Spending;
import ru.thecop.smtm2.util.DateTimeConverter;

import java.util.*;

public class FakeDb {

    private static final String TAG = "FakeDb";

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

    public static List<Spending> findSpendingsNonConfirmed() {
        List<Spending> result = new ArrayList<>();
        for (Spending spending : spendingTable.values()) {
            if (!spending.isConfirmed()) {
                result.add(spending);
            }
        }
        return result;
    }

    public static long saveSpending(Spending s) {
        if (s.getId() == null) {
            s.setId(nextSpendingId());
            Log.d(TAG, "Saving new spending " + s.toString());
        } else {
            Log.d(TAG, "Saving editied spending " + s.toString());
        }
        spendingTable.put(s.getId(), s);
        return s.getId();
    }

    private static long nextSpendingId() {
        long max = Long.MIN_VALUE;
        for (Long aLong : spendingTable.keySet()) {
            if (max < aLong) {
                max = aLong;
            }
        }
        return max + 1;
    }


    private static void fillSpendings() {
        createSpendingInTable(1L, new LocalDateTime(2017, 7, 12, 12, 21), 3.50, 1L,true);
        createSpendingInTable(2L, new LocalDateTime(2017, 7, 12, 13, 33), 666, 1L,false);
        createSpendingInTable(3L, new LocalDateTime(2017, 7, 13, 23, 59), 777.13, 1L,false);

        createSpendingInTable(4L, new LocalDateTime(2017, 7, 12, 0, 1), 63.55, 2L,false);
        createSpendingInTable(5L, new LocalDateTime(2017, 7, 12, 0, 3), 33.11, 2L,false);
        createSpendingInTable(6L, new LocalDateTime(2017, 7, 13, 0, 1), 36.6, 2L,true);

        createSpendingInTable(7L, new LocalDateTime(2017, 7, 12, 22, 17), 634, 3L,false);
        createSpendingInTable(8L, new LocalDateTime(2017, 7, 12, 14, 56), 335.1, 3L,false);
        createSpendingInTable(9L, new LocalDateTime(2017, 7, 13, 11, 3), 22.33, 3L,true);
    }

    private static void fillCategories() {
        createCategoryInTable(1L, "Продукты");
        createCategoryInTable(2L, "Электроника очень длинное название категории, вряд ли такое будет в реальной жизни, но мало ли наркоманов на свете");
        createCategoryInTable(3L, "Кино");

    }

    private static Spending createSpendingInTable(Long id, LocalDateTime localDateTime, double amount, long categoryId, boolean confirmed) {
        if (findCategoryById(categoryId) == null) {
            throw new IllegalArgumentException("categoryId");
        }
        Spending s = new Spending();
        s.setId(id);
        s.setTimestamp(DateTimeConverter.convert(localDateTime));
        s.setAmount(amount);
        s.setCategoryId(categoryId);
        s.setUid(UUID.randomUUID().toString());
        s.setConfirmed(confirmed);
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
