package ru.thecop.smtm2.db;

import android.util.Log;
import org.joda.time.LocalDateTime;
import ru.thecop.smtm2.SmtmApplication;
import ru.thecop.smtm2.model.Category;
import ru.thecop.smtm2.model.CategoryDao;
import ru.thecop.smtm2.model.Spending;
import ru.thecop.smtm2.model.SpendingDao;
import ru.thecop.smtm2.util.DateTimeConverter;

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
        return categoryDao.queryBuilder().orderDesc(CategoryDao.Properties.LowerCaseName).list();
    }

    public static List<Category> findCategoriesNameContains(String string, SmtmApplication application) {
        CategoryDao categoryDao = application.getDaoSession().getCategoryDao();
        //TODO screen string?
        String like = "%" + string.trim().toLowerCase() + "%";
        return categoryDao.queryBuilder()
                .where(CategoryDao.Properties.LowerCaseName.like(like))
                .orderDesc(CategoryDao.Properties.LowerCaseName)
                .list();
    }
}