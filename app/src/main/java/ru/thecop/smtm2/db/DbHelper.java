package ru.thecop.smtm2.db;

import android.database.Cursor;
import android.util.Log;
import org.joda.time.LocalDateTime;
import ru.thecop.smtm2.db.dto.CategoryStat;
import ru.thecop.smtm2.model.Category;
import ru.thecop.smtm2.model.CategoryDao;
import ru.thecop.smtm2.model.CategoryKeyword;
import ru.thecop.smtm2.model.CategoryKeywordDao;
import ru.thecop.smtm2.model.Spending;
import ru.thecop.smtm2.model.SpendingDao;
import ru.thecop.smtm2.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//todo separate file to spendings/categories/keywords
public final class DbHelper {
    private static final String TAG = "DbHelper";
    //todo add thread.sleeps to test loading progressbars

    private DbHelper() {
    }

    //Spendings-----------------------------------------------------------------

    private static void prepareNewSpending(Spending spending) {
        spending.setUpdatedTimestamp(DateTimeUtils.convert(LocalDateTime.now()));
        spending.setUid(UUID.randomUUID().toString());
        spending.setDeleted(false);
    }

    public static Spending create(Spending spending, SessionHolder holder) {
        prepareNewSpending(spending);

        SpendingDao spendingDao = holder.getSession().getSpendingDao();
        spendingDao.insert(spending);
        Log.d(TAG, "Inserted new spending " + spending.toString());
        return spending;
    }

    public static void createInSingleTransaction(List<Spending> spendings, SessionHolder holder) {
        holder.getSession().getDatabase().beginTransaction();
        SpendingDao spendingDao = holder.getSession().getSpendingDao();
        try {
            for (Spending spending : spendings) {
                prepareNewSpending(spending);
                spendingDao.insert(spending);
                Log.d(TAG, "Inserted new spending " + spending.toString());
            }
            holder.getSession().getDatabase().setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Failed to insert spendings in transaction: " + e.getMessage());
        } finally {
            holder.getSession().getDatabase().endTransaction();
        }
    }

    public static void update(Spending spending, SessionHolder holder) {
        spending.setUpdatedTimestamp(DateTimeUtils.convert(LocalDateTime.now()));
        SpendingDao spendingDao = holder.getSession().getSpendingDao();
        spendingDao.update(spending);
        Log.d(TAG, "Updated spending " + spending.toString());
    }

    public static List<Spending> findAllNonConfirmedSpendings(SessionHolder holder) {
        SpendingDao spendingDao = holder.getSession().getSpendingDao();
        return spendingDao.queryBuilder()
                .where(SpendingDao.Properties.Confirmed.eq(false))
                .orderDesc(SpendingDao.Properties.Timestamp)
                .list();
    }

    public static List<Spending> findAllConfirmedSpendings(SessionHolder holder) {
        SpendingDao spendingDao = holder.getSession().getSpendingDao();
        return spendingDao.queryBuilder()
                .where(SpendingDao.Properties.Confirmed.eq(true))
                .orderDesc(SpendingDao.Properties.Timestamp)
                .list();
    }

    public static List<Spending> findConfirmedSpendings(SessionHolder holder, long timestampFrom, long timestampTo) {
        SpendingDao spendingDao = holder.getSession().getSpendingDao();
        return spendingDao.queryBuilder()
                .where(SpendingDao.Properties.Confirmed.eq(true),
                        SpendingDao.Properties.Timestamp.ge(timestampFrom),
                        SpendingDao.Properties.Timestamp.le(timestampTo))
                .orderDesc(SpendingDao.Properties.Timestamp)
                .list();
    }

    public static Spending findSpendingById(long id, SessionHolder holder) {
        SpendingDao spendingDao = holder.getSession().getSpendingDao();
        return spendingDao.load(id);
    }

    public static Spending findLatestSpending(SessionHolder holder) {
        SpendingDao spendingDao = holder.getSession().getSpendingDao();
        List<Spending> list = spendingDao.queryBuilder()
                .orderDesc(SpendingDao.Properties.Timestamp)
                .limit(1)
                .list();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static Spending findEarliestSpending(SessionHolder holder) {
        SpendingDao spendingDao = holder.getSession().getSpendingDao();
        List<Spending> list = spendingDao.queryBuilder()
                .orderAsc(SpendingDao.Properties.Timestamp)
                .limit(1)
                .list();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    //Categories-----------------------------------------------------------------

    public static Category create(Category category, SessionHolder holder) {
        category.setUpdatedTimestamp(DateTimeUtils.convert(LocalDateTime.now()));
        category.setLowerCaseName(category.getName().toLowerCase());

        CategoryDao categoryDao = holder.getSession().getCategoryDao();
        categoryDao.insert(category);
        Log.d(TAG, "Inserted new category " + category.toString());
        return category;
    }

    public static Category findCategoryById(long id, SessionHolder holder) {
        CategoryDao categoryDao = holder.getSession().getCategoryDao();
        return categoryDao.load(id);
    }

    public static List<Category> findAllCategories(SessionHolder holder) {
        CategoryDao categoryDao = holder.getSession().getCategoryDao();
        return categoryDao.queryBuilder()
                .orderAsc(CategoryDao.Properties.LowerCaseName)
                .list();
    }

    public static List<Category> findCategoriesNameContains(String string, SessionHolder holder) {
        CategoryDao categoryDao = holder.getSession().getCategoryDao();
        //TODO screen/escape string?
        String like = "%" + string.trim().toLowerCase() + "%";
        return categoryDao.queryBuilder()
                .where(CategoryDao.Properties.LowerCaseName.like(like))
                .orderAsc(CategoryDao.Properties.LowerCaseName)
                .list();
    }

    public static List<CategoryStat> loadCategoriesStats(SessionHolder holder,
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
        Cursor cursor = holder.getSession().getDatabase().rawQuery(sql, null);
        List<CategoryStat> result = new ArrayList<>(cursor.getCount());
        try {
            if (cursor.moveToFirst()) {
                do {
                    long categoryId = cursor.getLong(cursor.getColumnIndex("catId"));
                    double totalAmount = cursor.getDouble(cursor.getColumnIndex("total"));
                    int entriesCount = cursor.getInt(cursor.getColumnIndex("entries"));
                    Category category = findCategoryById(categoryId, holder);
                    result.add(new CategoryStat(category, totalAmount, entriesCount));
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return result;
    }

    //Category keywords-----------------------------------------------------------------
    public static List<CategoryKeyword> findAllCategoryKeywords(SessionHolder holder) {
        CategoryKeywordDao keywordDao = holder.getSession().getCategoryKeywordDao();
        return keywordDao.queryBuilder()
                .orderAsc(CategoryKeywordDao.Properties.Keyword)
                .list();
    }
}
