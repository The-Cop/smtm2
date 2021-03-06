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

    public static Spending create(Spending spending, ContextAndSessionHolder holder) {
        prepareNewSpending(spending);

        SpendingDao spendingDao = holder.getSession().getSpendingDao();
        spendingDao.insert(spending);
        Log.d(TAG, "Inserted new spending " + spending.toString());
        return spending;
    }

    public static void createSpendingsInSingleTransaction(List<Spending> spendings, ContextAndSessionHolder holder) {
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

    public static void update(Spending spending, ContextAndSessionHolder holder) {
        spending.setUpdatedTimestamp(DateTimeUtils.convert(LocalDateTime.now()));
        SpendingDao spendingDao = holder.getSession().getSpendingDao();
        spendingDao.update(spending);
        Log.d(TAG, "Updated spending " + spending.toString());
    }

    public static List<Spending> findAllNonConfirmedSpendings(ContextAndSessionHolder holder) {
        SpendingDao spendingDao = holder.getSession().getSpendingDao();
        return spendingDao.queryBuilder()
                .where(SpendingDao.Properties.Confirmed.eq(false))
                .orderDesc(SpendingDao.Properties.Timestamp)
                .list();
    }

    public static List<Spending> findAllConfirmedSpendings(ContextAndSessionHolder holder) {
        SpendingDao spendingDao = holder.getSession().getSpendingDao();
        return spendingDao.queryBuilder()
                .where(SpendingDao.Properties.Confirmed.eq(true))
                .orderDesc(SpendingDao.Properties.Timestamp)
                .list();
    }

    public static List<Spending> findConfirmedSpendings(long timestampFrom, long timestampTo, ContextAndSessionHolder holder) {
        SpendingDao spendingDao = holder.getSession().getSpendingDao();
        return spendingDao.queryBuilder()
                .where(SpendingDao.Properties.Confirmed.eq(true),
                        SpendingDao.Properties.Timestamp.ge(timestampFrom),
                        SpendingDao.Properties.Timestamp.le(timestampTo))
                .orderDesc(SpendingDao.Properties.Timestamp)
                .list();
    }

    public static Spending findSpendingById(long id, ContextAndSessionHolder holder) {
        SpendingDao spendingDao = holder.getSession().getSpendingDao();
        return spendingDao.load(id);
    }

    public static Spending findLatestSpending(ContextAndSessionHolder holder) {
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

    public static Spending findEarliestSpending(ContextAndSessionHolder holder) {
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

    public static Category create(Category category, ContextAndSessionHolder holder) {
        category.setUpdatedTimestamp(DateTimeUtils.convert(LocalDateTime.now()));
        category.setLowerCaseName(category.getName().toLowerCase());

        CategoryDao categoryDao = holder.getSession().getCategoryDao();
        categoryDao.insert(category);
        Log.d(TAG, "Inserted new category " + category.toString());
        return category;
    }

    public static void createCategoriesInSingleTransaction(List<Category> categories, ContextAndSessionHolder holder) {
        holder.getSession().getDatabase().beginTransaction();
        CategoryDao categoryDao = holder.getSession().getCategoryDao();
        try {
            for (Category category : categories) {
                category.setUpdatedTimestamp(DateTimeUtils.convert(LocalDateTime.now()));
                category.setLowerCaseName(category.getName().toLowerCase());
                categoryDao.insert(category);
                Log.d(TAG, "Inserted new category " + category.toString());
            }
            holder.getSession().getDatabase().setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Failed to insert categories in transaction: " + e.getMessage());
        } finally {
            holder.getSession().getDatabase().endTransaction();
        }
    }

    public static Category findCategoryById(long id, ContextAndSessionHolder holder) {
        CategoryDao categoryDao = holder.getSession().getCategoryDao();
        return categoryDao.load(id);
    }

    public static List<Category> findAllCategories(ContextAndSessionHolder holder) {
        CategoryDao categoryDao = holder.getSession().getCategoryDao();
        return categoryDao.queryBuilder()
                .orderAsc(CategoryDao.Properties.LowerCaseName)
                .list();
    }

    public static List<Category> findCategoriesNameContains(String string, ContextAndSessionHolder holder) {
        CategoryDao categoryDao = holder.getSession().getCategoryDao();
        //TODO screen/escape string?
        String like = "%" + string.trim().toLowerCase() + "%";
        return categoryDao.queryBuilder()
                .where(CategoryDao.Properties.LowerCaseName.like(like))
                .orderAsc(CategoryDao.Properties.LowerCaseName)
                .list();
    }

    public static List<CategoryStat> loadCategoriesStats(long spendingsTimestampFrom,
                                                         long spendingsTimestampTo,
                                                         ContextAndSessionHolder holder) {
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
    public static List<CategoryKeyword> findAllCategoryKeywords(ContextAndSessionHolder holder) {
        CategoryKeywordDao keywordDao = holder.getSession().getCategoryKeywordDao();
        return keywordDao.queryBuilder()
                .orderAsc(CategoryKeywordDao.Properties.Keyword)
                .list();
    }

    public static List<CategoryKeyword> findCategoryKeywords(long categoryId, ContextAndSessionHolder holder) {
        CategoryKeywordDao keywordDao = holder.getSession().getCategoryKeywordDao();
        return keywordDao.queryBuilder()
                .where(CategoryKeywordDao.Properties.CategoryId.eq(categoryId))
                .orderAsc(CategoryKeywordDao.Properties.Keyword)
                .list();
    }

    public static CategoryKeyword findCategoryKeywordByKeyword(String keyword, ContextAndSessionHolder holder) {
        CategoryKeywordDao keywordDao = holder.getSession().getCategoryKeywordDao();
        return keywordDao.queryBuilder()
                .where(CategoryKeywordDao.Properties.Keyword.eq(keyword))
                .unique();
    }

    public static CategoryKeyword createCategoryKeyword(String keyword, long categoryId, ContextAndSessionHolder holder) {
        CategoryKeywordDao keywordDao = holder.getSession().getCategoryKeywordDao();
        CategoryKeyword categoryKeyword = new CategoryKeyword();
        categoryKeyword.setCategoryId(categoryId);
        categoryKeyword.setKeyword(keyword.toLowerCase());

        keywordDao.insert(categoryKeyword);
        Log.d(TAG, "Inserted new categoryKeyword " + categoryKeyword.toString());
        return categoryKeyword;
    }

    public static boolean deleteCategoryKeyword(String keyword, ContextAndSessionHolder holder) {
        CategoryKeywordDao keywordDao = holder.getSession().getCategoryKeywordDao();
        CategoryKeyword categoryKeyword = findCategoryKeywordByKeyword(keyword, holder);
        if (categoryKeyword == null) {
            Log.d(TAG, "Failed to delete categoryKeyword " + keyword + " - not found");
            return false;
        }

        keywordDao.delete(categoryKeyword);
        Log.d(TAG, "Deleted categoryKeyword " + categoryKeyword.toString());
        return true;
    }

}
