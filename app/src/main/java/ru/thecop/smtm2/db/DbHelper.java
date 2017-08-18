package ru.thecop.smtm2.db;

import android.database.Cursor;
import android.util.Log;
import org.joda.time.LocalDateTime;
import ru.thecop.smtm2.SmtmApplication;
import ru.thecop.smtm2.model.Category;
import ru.thecop.smtm2.model.CategoryDao;
import ru.thecop.smtm2.model.Spending;
import ru.thecop.smtm2.model.SpendingDao;
import ru.thecop.smtm2.util.DateTimeConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DbHelper {
    private static final String TAG = "DbHelper";
    //todo add thread.sleeps to test loading progressbars

    private DbHelper() {
    }

    //Spendings-----------------------------------------------------------------

    public static Spending create(Spending spending, SmtmApplication application) {
        spending.setUpdatedTimestamp(DateTimeConverter.convert(LocalDateTime.now()));
        spending.setUid(UUID.randomUUID().toString());
        spending.setDeleted(false);

        SpendingDao spendingDao = application.getDaoSession().getSpendingDao();
        spendingDao.insert(spending);
        Log.d(TAG, "Inserted new spending " + spending.toString());
        return spending;
    }

    public static void update(Spending spending, SmtmApplication application) {
        spending.setUpdatedTimestamp(DateTimeConverter.convert(LocalDateTime.now()));
        SpendingDao spendingDao = application.getDaoSession().getSpendingDao();
        spendingDao.update(spending);
        Log.d(TAG, "Updated spending " + spending.toString());
    }

    public static List<Spending> findAllNonConfirmedSpendings(SmtmApplication application) {
        SpendingDao spendingDao = application.getDaoSession().getSpendingDao();
        return spendingDao.queryBuilder()
                .where(SpendingDao.Properties.Confirmed.eq(false))
                .orderDesc(SpendingDao.Properties.Timestamp)
                .list();
    }

    public static List<Spending> findAllConfirmedSpendings(SmtmApplication application) {
        SpendingDao spendingDao = application.getDaoSession().getSpendingDao();
        return spendingDao.queryBuilder()
                .where(SpendingDao.Properties.Confirmed.eq(true))
                .orderDesc(SpendingDao.Properties.Timestamp)
                .list();
    }

    public static Spending findSpendingById(long id, SmtmApplication application) {
        SpendingDao spendingDao = application.getDaoSession().getSpendingDao();
        return spendingDao.load(id);
    }

    //Categories-----------------------------------------------------------------

    public static Category create(Category category, SmtmApplication application) {
        category.setUpdatedTimestamp(DateTimeConverter.convert(LocalDateTime.now()));
        category.setLowerCaseName(category.getName().toLowerCase());

        CategoryDao categoryDao = application.getDaoSession().getCategoryDao();
        categoryDao.insert(category);
        Log.d(TAG, "Inserted new category " + category.toString());
        return category;
    }

    public static Category findCategoryById(long id, SmtmApplication application) {
        CategoryDao categoryDao = application.getDaoSession().getCategoryDao();
        return categoryDao.load(id);
    }

    public static List<Category> findAllCategories(SmtmApplication application) {
        CategoryDao categoryDao = application.getDaoSession().getCategoryDao();
        return categoryDao.queryBuilder()
                .orderAsc(CategoryDao.Properties.LowerCaseName)
                .list();
    }

    public static List<Category> findCategoriesNameContains(String string, SmtmApplication application) {
        CategoryDao categoryDao = application.getDaoSession().getCategoryDao();
        //TODO screen string?
        String like = "%" + string.trim().toLowerCase() + "%";
        return categoryDao.queryBuilder()
                .where(CategoryDao.Properties.LowerCaseName.like(like))
                .orderAsc(CategoryDao.Properties.LowerCaseName)
                .list();
    }

    public static List<CategoryStatsRequestResult> loadCategoriesStats(SmtmApplication application,
                                                                       long spendingsTimestampFrom,
                                                                       long spendingsTimestampTo) {
        String sql = "select ct." + CategoryDao.Properties.Id.columnName + " as catId"
                + ", sum(sp." + SpendingDao.Properties.Amount.columnName + ") as total"
                + ", count(sp." + SpendingDao.Properties.Id.columnName + ") as entries"
                + " from " + SpendingDao.TABLENAME + " sp, " + CategoryDao.TABLENAME + " ct "
                + " where sp." + SpendingDao.Properties.CategoryId.columnName + "=ct." + CategoryDao.Properties.Id.columnName
                + " and sp." + SpendingDao.Properties.Timestamp.columnName + ">=" + spendingsTimestampFrom
                + " and sp." + SpendingDao.Properties.Timestamp.columnName + "<=" + spendingsTimestampTo
                + " group by ct." + CategoryDao.Properties.Id.columnName
                + " order by total desc";
        Log.d(TAG, "loadCategoriesStats sql = " + sql);
        Cursor cursor = application.getDaoSession().getDatabase().rawQuery(sql, null);
        List<CategoryStatsRequestResult> result = new ArrayList<>(cursor.getCount());
        try {
            if (cursor.moveToFirst()) {
                do {
                    long categoryId = cursor.getLong(cursor.getColumnIndex("catId"));
                    double totalAmount = cursor.getDouble(cursor.getColumnIndex("total"));
                    int entriesCount = cursor.getInt(cursor.getColumnIndex("entries"));
                    Category category = findCategoryById(categoryId, application);
                    result.add(new CategoryStatsRequestResult(category, totalAmount, entriesCount));
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return result;
    }
}
